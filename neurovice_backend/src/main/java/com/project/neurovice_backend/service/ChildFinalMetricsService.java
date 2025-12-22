package com.project.neurovice_backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.neurovice_backend.model.ChildFinalMetricsEntity;
import com.project.neurovice_backend.model.GameMetricsEntity;
import com.project.neurovice_backend.model.assessment_sections;
import com.project.neurovice_backend.repository.ChildFinalMetricsRepository;
import com.project.neurovice_backend.repository.GameMetricsRepository;
import com.project.neurovice_backend.repository.assessmentRepository;
import com.project.neurovice_backend.repository.assessmentSectionsRepository;

@Service
public class ChildFinalMetricsService {

    private final GameMetricsRepository gameMetricsRepository;
    private final ChildFinalMetricsRepository childFinalMetricsRepository;
    private final assessmentRepository assessmentRepository;
    private final assessmentSectionsRepository assessmentSectionRepository;

    public ChildFinalMetricsService(
            GameMetricsRepository gameMetricsRepository,
            ChildFinalMetricsRepository childFinalMetricsRepository,
            assessmentRepository assessmentRepository,
            assessmentSectionsRepository assessmentSectionRepository) {

        this.gameMetricsRepository = gameMetricsRepository;
        this.childFinalMetricsRepository = childFinalMetricsRepository;
        this.assessmentRepository = assessmentRepository;
        this.assessmentSectionRepository = assessmentSectionRepository;
    }

    // ============================
    // ENTRY POINT
    // ============================
    @Transactional
    public void computeAndStoreFinalMetrics(Long childId) {

        // Fetch all game sessions for child
        List<GameMetricsEntity> sessions
                = gameMetricsRepository.findByChildIdOrderByCreatedAtAsc(childId);

        if (sessions.isEmpty()) {
            return; // nothing to compute
        }

        // Latest session
        GameMetricsEntity latest = sessions.get(sessions.size() - 1);

        // Historical sessions (exclude latest)
        List<GameMetricsEntity> history
                = sessions.subList(0, sessions.size() - 1);

        // Fetch questionnaire scores
        Integer assessmentId = assessmentRepository
                .findLatestAssessmentIdByChildId(childId);

        Map<String, Double> questionnaireScores
                = getQuestionnaireScores(assessmentId);

        // Compute final metrics
        double finalInattention = calculateFinalMetric(
                questionnaireScores.get("INATTENTION"),
                latest.getInattention(),
                average(history, GameMetricsEntity::getInattention)
        );

        double finalHyperactivity = calculateFinalMetric(
                questionnaireScores.get("HYPERACTIVITY"),
                latest.getHyperactivity(),
                average(history, GameMetricsEntity::getHyperactivity)
        );

        double finalADHD = calculateFinalMetric(
                questionnaireScores.get("ADHD"),
                latest.getAdhdComposite(),
                average(history, GameMetricsEntity::getAdhdComposite)
        );

        double finalODD = calculateFinalMetric(
                questionnaireScores.get("ODD"),
                latest.getOddIndex(),
                average(history, GameMetricsEntity::getOddIndex)
        );

        double finalConduct = calculateFinalMetric(
                questionnaireScores.get("CONDUCT"),
                latest.getConductIndex(),
                average(history, GameMetricsEntity::getConductIndex)
        );

        double finalAnxiety = calculateFinalMetric(
                questionnaireScores.get("ANXIETY"),
                latest.getAnxietyIndex(),
                average(history, GameMetricsEntity::getAnxietyIndex)
        );

        // Insert or update final metrics table
        ChildFinalMetricsEntity entity
                = childFinalMetricsRepository
                        .findByChildId(childId)
                        .orElse(new ChildFinalMetricsEntity());

        entity.setChildId(childId);
        entity.setGameId("ADHD"); // fixed for now
        entity.setSessionCount(sessions.size());

        entity.setInattention(finalInattention);
        entity.setHyperactivity(finalHyperactivity);
        entity.setAdhdComposite(finalADHD);
        entity.setOddIndex(finalODD);
        entity.setConductIndex(finalConduct);
        entity.setAnxietyIndex(finalAnxiety);

        entity.setComputedAt(LocalDateTime.now());

        childFinalMetricsRepository.save(entity);

        // LOG EVERYTHING
        logMetrics(entity);
    }

    // ============================
    // CORE WEIGHTED FORMULA
    // ============================
    private double calculateFinalMetric(
            Double questionnaire,
            Double latest,
            Double historyAvg) {

        if (questionnaire == null) {
            questionnaire = 0.0;
        }
        if (latest == null) {
            latest = 0.0;
        }
        if (historyAvg == null) {
            historyAvg = 0.0;
        }

        double trend = latest - historyAvg;

        return 0.4 * questionnaire
                + 0.3 * latest
                + 0.2 * historyAvg
                + 0.1 * trend;
    }

    // ============================
    // QUESTIONNAIRE HELPERS
    // ============================
    private Map<String, Double> getQuestionnaireScores(Integer assessmentId) {

        Map<String, Double> scores = new HashMap<>();

        if (assessmentId == null) {
            scores.put("INATTENTION", 0.0);
            scores.put("HYPERACTIVITY", 0.0);
            scores.put("ADHD", 0.0);
            scores.put("ODD", 0.0);
            scores.put("CONDUCT", 0.0);
            scores.put("ANXIETY", 0.0);
            return scores;
        }

        List<assessment_sections> sections
                = assessmentSectionRepository
                        .findByAssessmentId(assessmentId);

        for (assessment_sections s : sections) {
            scores.put(s.getSectionId(), s.getAvgScore());
        }

        return scores;
    }

    // ============================
    // HISTORY AVERAGE HELPER
    // ============================
    private double average(
            List<GameMetricsEntity> list,
            Function<GameMetricsEntity, Double> extractor) {

        if (list == null || list.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;

        for (GameMetricsEntity e : list) {
            Double value = extractor.apply(e);
            if (value != null) {
                sum += value;
                count++;
            }
        }

        return count == 0 ? 0.0 : sum / count;
    }

    private void logMetrics(ChildFinalMetricsEntity e) {
        System.out.println("Computed Game Metrics for Child ID: " + e.getChildId());
        System.out.println("Hyperactivity: " + e.getHyperactivity());
        System.out.println("Inattention: " + e.getInattention());
        System.out.println("ADHD Composite: " + e.getAdhdComposite());
        System.out.println("ODD Index: " + e.getOddIndex());
        System.out.println("Conduct Index: " + e.getConductIndex());
        System.out.println("Anxiety Index: " + e.getAnxietyIndex());
    }
}
