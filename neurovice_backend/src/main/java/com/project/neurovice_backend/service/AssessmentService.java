package com.project.neurovice_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
}
