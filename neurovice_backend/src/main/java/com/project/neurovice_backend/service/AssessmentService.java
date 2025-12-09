package com.project.neurovice_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.neurovice_backend.dto.*;
import com.project.neurovice_backend.exception.BadRequestException;
import com.project.neurovice_backend.exception.NotFoundException;
import com.project.neurovice_backend.model.*;
import com.project.neurovice_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final assessmentRepository assessmentRepository;
    private final assessmentSectionsRepository assessmentSectionsRepository;
    private final childRepository childRepository;
    private final questionairesRepository questionnaireRepository;
    private final QuestionnaireService questionnaireService;
    private final QuestionnaireMetricsRepository questionnaireMetricsRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public StartAssessmentResponse startAssessment(Integer childId) {
        if (!childRepository.existsById(Long.valueOf(childId))) {
            throw new NotFoundException("Child not found");
        }
        assessment assessment = new assessment();
        assessment.setChildId(childId);
        assessment.setStatus("IN_PROGRESS");
        assessment.setCreatedAt(LocalDateTime.now());
        assessment = assessmentRepository.save(assessment);
        return new StartAssessmentResponse(Math.toIntExact(assessment.getAssessmentId()));
    }

    @Transactional
    public SectionSubmitResponse submitSection(Integer assessmentId, SectionSubmitRequest request) {
        assessment assessment = assessmentRepository.findById(Long.valueOf(assessmentId))
                .orElseThrow(() -> new NotFoundException("Assessment not found"));

        questionaires questionnaire = questionnaireRepository
                .findBySectionId(request.getSectionId())
                .orElseThrow(() -> new NotFoundException("Section not found"));

        // Validate answers
        Map<String, Integer> answers = request.getAnswers();
        List<?> questions = (List<?>) questionnaire.getQuestions();
        if (answers.size() != questions.size()) {
            throw new BadRequestException("Answer count does not match questions");
        }
        for (Map.Entry<String, Integer> e : answers.entrySet()) {
            int v = e.getValue();
            if (v < 1 || v > 5) throw new BadRequestException("Answer values must be between 1 and 5");
        }

        // Calculate scores
        int sum = answers.values().stream().mapToInt(Integer::intValue).sum();
        double avg = answers.values().stream().mapToInt(Integer::intValue).average().orElse(0);

        // UPSERT into assessment_sections
        try {
            String answersJson = objectMapper.writeValueAsString(answers);

            String upsertSql = """
                INSERT INTO assessment_sections
                    (assessment_id, section_id, disorder_type, question_response, sum_score, avg_score, created_at)
                VALUES
                    (:assessmentId, :sectionId, :disorderType, CAST(:questionResponse AS JSONB), :sumScore, :avgScore, NOW())
                ON CONFLICT (assessment_id, section_id)
                DO UPDATE SET
                    question_response=EXCLUDED.question_response,
                    sum_score=EXCLUDED.sum_score,
                    avg_score=EXCLUDED.avg_score;
                """;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("assessmentId", assessmentId)
                    .addValue("sectionId", request.getSectionId())
                    .addValue("disorderType", questionnaire.getDisorderType())
                    .addValue("questionResponse", answersJson)
                    .addValue("sumScore", sum)
                    .addValue("avgScore", avg);

            jdbcTemplate.update(upsertSql, params);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Error processing answers to JSON");
        }

        // Completed logic
        int submittedSections = assessmentSectionsRepository.countDistinctSectionsByAssessmentId(assessmentId);
        int requiredSections = questionnaireService.countActiveSections();

        boolean allSectionsCompleted = submittedSections >= requiredSections;
        if (allSectionsCompleted && !"COMPLETED".equals(assessment.getStatus())) {
            assessment.setStatus("COMPLETED");
            assessment.setCompletedAt(LocalDateTime.now());
            assessmentRepository.save(assessment);
            // Compute Vanderbilt diagnosis after all sections are completed
            computeDiagnosis(assessmentId, assessment.getChildId());
        }

        return new SectionSubmitResponse(true, allSectionsCompleted);
    }

    public AssessmentStatusResponse getStatus(Integer assessmentId) {
        assessment assessment = assessmentRepository.findById(Long.valueOf(assessmentId))
                .orElseThrow(() -> new NotFoundException("Assessment not found"));
        List<String> submittedSections = assessmentSectionsRepository.findSectionIdsByAssessmentId(assessmentId);
        AssessmentStatusResponse response = new AssessmentStatusResponse();
        response.setStatus(assessment.getStatus());
        response.setCreatedAt(assessment.getCreatedAt());
        response.setCompletedAt(assessment.getCompletedAt());
        response.setSubmittedSections(submittedSections);
        return response;
    }

    /**
     * Retrieves the diagnosis results for a completed assessment.
     */
    public DiagnosisResponse getDiagnosis(Integer assessmentId) {
        QuestionnaireMetrics metrics = questionnaireMetricsRepository
                .findByAssessmentId(assessmentId)
                .orElseThrow(() -> new NotFoundException("Diagnosis not found for this assessment. Assessment may not be completed yet."));

        return new DiagnosisResponse(
                metrics.getInattention() == 1,
                metrics.getHyperactivity() == 1,
                metrics.getCombined() == 1,
                metrics.getOdd() == 1,
                metrics.getConduct() == 1,
                metrics.getAnxietyDepression() == 1
        );
    }

    /**
     * Computes Vanderbilt diagnosis based on official scoring rules from page 10.
     * Called automatically when all sections are completed.
     */
    @Transactional
    public void computeDiagnosis(Integer assessmentId, Integer childId) {
        Map<Integer, Integer> answers = loadAllQuestionAnswers(assessmentId);

        // Count scores (2 or 3) for each disorder category
        int inattentionCount = countScores(answers, 1, 9, 2, 3); // Q1-Q9: 6+ required
        int hyperactivityCount = countScores(answers, 10, 18, 2, 3); // Q10-Q18: 6+ required
        int oddCount = countScores(answers, 19, 26, 2, 3); // Q19-Q26: 4+ required
        int conductCount = countScores(answers, 27, 40, 2, 3); // Q27-Q40: 3+ required
        int anxDepCount = countScores(answers, 41, 47, 2, 3); // Q41-Q47: 3+ required

        // Check performance questions (Q48-Q55): any score 4 or 5 indicates impairment
        boolean performanceFlag = hasPerformanceIssues(answers);

        // Apply Vanderbilt criteria
        boolean inattentive = (inattentionCount >= 6 && performanceFlag);
        boolean hyperactive = (hyperactivityCount >= 6 && performanceFlag);
        boolean combined = (inattentive && hyperactive);
        boolean odd = (oddCount >= 4 && performanceFlag);
        boolean conduct = (conductCount >= 3 && performanceFlag);
        boolean anxietyDepression = (anxDepCount >= 3 && performanceFlag);

        // Save or update metrics
        QuestionnaireMetrics metrics = questionnaireMetricsRepository
                .findByAssessmentId(assessmentId)
                .orElse(new QuestionnaireMetrics());

        metrics.setAssessmentId(assessmentId);
        metrics.setChildId(childId);
        metrics.setInattention(inattentive ? 1 : 0);
        metrics.setHyperactivity(hyperactive ? 1 : 0);
        metrics.setCombined(combined ? 1 : 0);
        metrics.setOdd(odd ? 1 : 0);
        metrics.setConduct(conduct ? 1 : 0);
        metrics.setAnxietyDepression(anxietyDepression ? 1 : 0);

        questionnaireMetricsRepository.save(metrics);
    }

    /**
     * Loads all Q1-Q55 answers from all sections for this assessment.
     * Returns a map keyed by question number (1-55).
     */
    private Map<Integer, Integer> loadAllQuestionAnswers(Integer assessmentId) {
        List<assessment_sections> sections = assessmentSectionsRepository.findByAssessmentId(assessmentId);
        Map<Integer, Integer> allAnswers = new HashMap<>();

        for (assessment_sections section : sections) {
            if (section.getQuestionResponse() == null) continue;

            try {
                // Convert Map<String, Object> to JSON string then parse as JsonNode
                String jsonString = objectMapper.writeValueAsString(section.getQuestionResponse());
                JsonNode node = objectMapper.readTree(jsonString);

                // Extract question numbers and answers
                Iterator<String> fieldNames = node.fieldNames();
                while (fieldNames.hasNext()) {
                    String key = fieldNames.next();
                    if (key.toLowerCase().startsWith("q")) {
                        try {
                            int questionNum = Integer.parseInt(key.substring(1));
                            int answerValue = node.get(key).asInt();
                            allAnswers.put(questionNum, answerValue);
                        } catch (NumberFormatException e) {
                            // Skip invalid question keys
                        }
                    }
                }
            } catch (Exception e) {
                // Log error but continue processing other sections
                System.err.println("Error parsing answers for section " + section.getSectionId() + ": " + e.getMessage());
            }
        }

        return allAnswers;
    }

    /**
     * Counts questions in range [start, end] where answer equals target1 OR target2.
     * Used for Vanderbilt scoring: counts answers of 2 or 3 (often/very often).
     */
    private int countScores(Map<Integer, Integer> answers, int start, int end, int target1, int target2) {
        int count = 0;
        for (int i = start; i <= end; i++) {
            Integer answer = answers.get(i);
            if (answer != null && (answer == target1 || answer == target2)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns true if any performance question (Q48-Q55) has score 4 or 5.
     * This indicates performance impairment required for diagnosis.
     */
    private boolean hasPerformanceIssues(Map<Integer, Integer> answers) {
        for (int i = 48; i <= 55; i++) {
            Integer answer = answers.get(i);
            if (answer != null && (answer == 4 || answer == 5)) {
                return true;
            }
        }
        return false;
    }
}
