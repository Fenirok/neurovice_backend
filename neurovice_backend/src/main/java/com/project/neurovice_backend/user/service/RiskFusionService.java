package com.project.neurovice_backend.user.service;

import org.springframework.stereotype.Service;

@Service
public class RiskFusionService {

    // Tier 1: Asymmetric Clinical Momentum Modifiers
    private static final double GAMMA_PENALTY = 0.4;
    private static final double GAMMA_REWARD = 0.1;

    // Tier 3: EWMA Smoothing Factor (Kalman-style update)
    private static final double EWMA_ALPHA = 0.3;

    /**
     * Orchestrates the three-tier temporal fusion pipeline.
     */
    public double generateFinalLongitudinalRisk(
            double currentRawGameScore, double previousRawGameScore,
            double rawQuestRisk, double gameVariance, double questVariance,
            Double lastFinalRisk) {

        // Tier 1: Evaluate Cognitive Trajectory
        double adjustedGameRisk = calculateAdjustedGameRisk(currentRawGameScore, previousRawGameScore);

        // Tier 2: Cross-Modal Fusion
        double riskToday = fuseRiskToday(adjustedGameRisk, rawQuestRisk, gameVariance, questVariance);

        // Tier 3: Temporal State Update (Handle first-time players safely)
        if (lastFinalRisk == null) {
            return riskToday;
        }

        return calculateFinalEWMA(riskToday, lastFinalRisk);
    }

    private double calculateAdjustedGameRisk(double currentRawGameScore, double previousRawGameScore) {
        double trend = currentRawGameScore - previousRawGameScore;
        // Apply heavier penalty if performance is degrading
        double gamma = (trend > 0) ? GAMMA_PENALTY : GAMMA_REWARD;
        return currentRawGameScore + (gamma * trend);
    }

    private double fuseRiskToday(double adjustedGameRisk, double rawQuestRisk, double gameVariance, double questVariance) {
        double totalVariance = gameVariance + questVariance;

        // Prevent division by zero
        if (totalVariance == 0) {
            return (adjustedGameRisk + rawQuestRisk) / 2.0;
        }

        // Calculate Inverse-Variance Weights
        double wQuest = gameVariance / totalVariance;
        double wGame = questVariance / totalVariance;

        return (wQuest * rawQuestRisk) + (wGame * adjustedGameRisk);
    }

    private double calculateFinalEWMA(double riskToday, double lastFinalRisk) {
        return (EWMA_ALPHA * riskToday) + ((1.0 - EWMA_ALPHA) * lastFinalRisk);
    }
}
