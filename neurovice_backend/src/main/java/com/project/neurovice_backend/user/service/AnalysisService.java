package com.project.neurovice_backend.user.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.neurovice_backend.user.dto.AnalysisResponse;
import com.project.neurovice_backend.user.exception.NotFoundException;
import com.project.neurovice_backend.user.model.ADHDAnalysisResult;
import com.project.neurovice_backend.user.model.ADHDRawGameMetrics;
import com.project.neurovice_backend.user.model.QuestionnaireMetrics;
import com.project.neurovice_backend.user.repository.ADHDAnalysisRepository;
import com.project.neurovice_backend.user.repository.GameMetricsRepository;
import com.project.neurovice_backend.user.repository.QuestionnaireMetricsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    @Autowired
    private final GameMetricsRepository gameMetricsRepository;
    private final QuestionnaireMetricsRepository questionnaireMetricsRepository;
    // private final QuestionnaireMetrics questionnaireMetrics;
    private final AiService aiService;
    private final ADHDAnalysisRepository adhdAnalysisRepository;
    private final RiskFusionService riskFusionService;

    // Static global variances derived from your Colab dataset
    private static final double GLOBAL_QUEST_VARIANCE = 40.0;
    private static final double GLOBAL_GAME_VARIANCE = 160.0;

    public AnalysisResponse run(Long childId) {
        // ==========================================
        // STEP 1: Fetch and Process Questionnaire Data
        // ==========================================
        QuestionnaireMetrics metrics = questionnaireMetricsRepository
                .findByChildId(childId)
                .orElseThrow(() -> new NotFoundException("Final metrics not found for child"));

        Map<String, Object> questFeatures = new HashMap<>();
        questFeatures.put("inattention", nz(metrics.getInattention()));
        questFeatures.put("hyperactivity", nz(metrics.getHyperactivity()));
        questFeatures.put("odd", nz(metrics.getOdd()));
        questFeatures.put("conduct", nz(metrics.getConduct()));
        questFeatures.put("anxiety", nz(metrics.getAnxiety()));
        questFeatures.put("performance_flag", nz(metrics.getPerformanceFlag()));

        Double rawQuestRisk = aiService.getQuestRisk(questFeatures);
        double finalRawQuestRisk = rawQuestRisk == null ? 0.0 : rawQuestRisk;

        // ==========================================
        // STEP 2: Fetch and Process Game Data (TODO)
        // ==========================================
        ADHDRawGameMetrics gameMetrics = gameMetricsRepository
                .findByChildId(childId)
                .orElseThrow(() -> new NotFoundException("Game metrics not found for child"));

        Map<String, Object> gameFeatures = new HashMap<>();
        gameFeatures.put("accuracy", nz(gameMetrics.getAccuracy()));
        gameFeatures.put("attention_decay", nz(gameMetrics.getAttentionDecay()));
        gameFeatures.put("randomness", nz(gameMetrics.getRandomness()));
        gameFeatures.put("burst_intensity", nz(gameMetrics.getBurstIntensity()));
        gameFeatures.put("spam_intensity", nz(gameMetrics.getSpamIntensity()));
        gameFeatures.put("direction_change_rate", nz(gameMetrics.getDirectionChangeRate()));
        gameFeatures.put("hold_impulsivity", nz(gameMetrics.getHoldImpulsivity()));

        Double finalRawGameRisk = aiService.getGameRisk(gameFeatures); // Assuming you add this to AiService
        // double finalRawGameRisk = 75.0; // MOCK DATA until DB is wired up

        // ==========================================
        // STEP 3: Fetch Historical State
        // ==========================================
        double previousRawGameScore;
        Double lastFinalRisk = null;        // null if first time

        // Query the DB for the child's most recent session using the new repository method
        Optional<ADHDAnalysisResult> historicalData = adhdAnalysisRepository
                .findFirstByChildIdOrderByLastUpdatedDesc(childId);

        if (historicalData.isPresent()) {
            ADHDAnalysisResult pastResult = historicalData.get();
            // Get the final smoothed score from their last session
            lastFinalRisk = pastResult.getScore();
            // Get the raw game score from their last session
            previousRawGameScore = nz(pastResult.getRawGameScore());
        } else {
            // First-time player: Use today's raw game score as the baseline 
            // to prevent an artificial momentum penalty on day one.
            previousRawGameScore = finalRawGameRisk;
        }

        // ==========================================
        // STEP 4: Execute the Temporal Math Engine
        // ==========================================
        double finalSmoothedRisk = riskFusionService.generateFinalLongitudinalRisk(
                finalRawGameRisk,
                previousRawGameScore,
                finalRawQuestRisk,
                GLOBAL_GAME_VARIANCE,
                GLOBAL_QUEST_VARIANCE,
                lastFinalRisk
        );

        // ==========================================
        // STEP 5: Save State and Return
        // ==========================================
        ADHDAnalysisResult result = new ADHDAnalysisResult();
        result.setChildId(childId);
        result.setScore(finalSmoothedRisk);
        result.setLastUpdated(LocalDateTime.now());
        adhdAnalysisRepository.save(result);

        System.out.println("Saved final temporal analysis result to DB: " + finalSmoothedRisk);

        return new AnalysisResponse(finalSmoothedRisk);
    }

    private double nz(Double v) {
        return v == null ? 0.0 : v;
    }

    private Integer nz(Integer v) {
        return v == null ? 0 : v;
    }
}
