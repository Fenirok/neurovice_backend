package com.project.neurovice_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.neurovice_backend.dto.GameDataRequest;
import com.project.neurovice_backend.model.GameMetricsEntity;
import com.project.neurovice_backend.repository.GameMetricsRepository;

@Service
public class GameMetricsService {

    private final GameMetricsRepository repository;
    @Autowired
    private ChildFinalMetricsService childFinalMetricsService;

    public GameMetricsService(GameMetricsRepository repository) {
        this.repository = repository;
    }

    public void processAndStore(GameDataRequest dto) {

        GameMetricsEntity entity = new GameMetricsEntity();

        // SET IDENTIFIERS FIRST
        entity.setChildId(1L);
        entity.setGameId("ADHD_GAME_1");
        entity.setSessionId("sess_" + System.currentTimeMillis());

        // ---------- RAW METRICS ----------
        entity.setTotalClicks(dto.getTotalClicks());
        entity.setBurstCount(dto.getBurstCount());
        entity.setArrowPressCount(dto.getArrowPressCount());
        entity.setArrowAvgInterval(dto.getArrowAvgInterval());
        entity.setArrowIntervalSum(dto.getArrowIntervalSum());
        entity.setArrowSpamRate(dto.getArrowSpamRate());
        entity.setDirectionChangeCount(dto.getDirectionChangeCount());
        entity.setTotalArrowHoldTime(dto.getTotalArrowHoldTime());

        entity.setScore0to60(dto.getScore_0_60());
        entity.setScore60to120(dto.getScore_60_120());
        entity.setScore120to180(dto.getScore_120_180());

        entity.setTotalTargetsAppeared(dto.getTotalTargetsAppeared());
        entity.setTotalTargetsCaught(dto.getTotalTargetsCaught());
        entity.setTimeLeft(dto.getTimeLeft());

        // ---------- BASE METRICS ----------
        entity.setAccuracy(calculateAccuracy(dto));
        entity.setAttentionDecay(calculateAttentionDecay(dto));
        entity.setRandomness(calculateArrowRandomness(dto));
        entity.setBurstIntensity(calculateBurstIntensity(dto));
        entity.setSpamIntensity(calculateSpamIntensity(dto));
        entity.setDirectionChangeRate(calculateDirectionChangeRate(dto));
        entity.setHoldImpulsivity(calculateHoldImpulsivity(dto));

        // ---------- DERIVED METRICS ----------
        entity.setHyperactivity(calculateHyperactivity(entity));
        entity.setInattention(calculateInattention(entity));

        // ------- EXTENDED BEHAVIORAL INDICES -------
        entity.setAdhdComposite(calculateADHDComposite(entity));
        entity.setOddIndex(calculateODDIndex(entity));
        entity.setConductIndex(calculateConductIndex(entity));
        entity.setAnxietyIndex(calculateAnxietyIndex(entity));

        entity.setCreatedAt(LocalDateTime.now());

        // LOG EVERYTHING
        logMetrics(entity);

        repository.save(entity);

        // TEMP: childId is always 1
        Long childId = 1L;

        // 🔥 Trigger final metrics computation
        childFinalMetricsService.computeAndStoreFinalMetrics(childId);
    }

    // ---------- BASE METRICS ----------
    private double calculateAccuracy(GameDataRequest dto) {
        if (dto.getTotalTargetsAppeared() == 0) {
            return 0.0;
        }
        return (double) dto.getTotalTargetsCaught()
                / dto.getTotalTargetsAppeared();
    }

    private double calculateAttentionDecay(GameDataRequest dto) {

        int early = dto.getScore_0_60();
        int late = dto.getScore_120_180();

        if (early == 0) {
            return 0.0;
        }

        return (double) (early - late) / early;
    }

    private double calculateArrowRandomness(GameDataRequest dto) {

        if (dto.getArrowPressCount() == 0) {
            return 0.0;
        }

        return (double) dto.getDirectionChangeCount()
                / dto.getArrowPressCount();
    }

    private double calculateBurstIntensity(GameDataRequest dto) {
        double timePlayed = 180.0 - dto.getTimeLeft();
        if (timePlayed <= 0) {
            timePlayed = 1.0;
        }

        return dto.getBurstCount() / timePlayed;
    }

    private double calculateSpamIntensity(GameDataRequest dto) {
        return (double) dto.getArrowSpamRate();
    }

    private double calculateDirectionChangeRate(GameDataRequest dto) {
        double timePlayed = 180.0 - dto.getTimeLeft();

        if (timePlayed <= 0) {
            timePlayed = 1.0;
        }

        return dto.getDirectionChangeCount() / timePlayed;
    }

    private double calculateHoldImpulsivity(GameDataRequest dto) {
        if (dto.getArrowPressCount() == 0) {
            return 0.0;
        }

        return dto.getTotalArrowHoldTime()
                / dto.getArrowPressCount();
    }

    // ===== METRIC CALCULATIONS =====
    private double calculateHyperactivity(GameMetricsEntity entity) {
        double spam = entity.getSpamIntensity();
        double burst = entity.getBurstIntensity();
        double direction = entity.getDirectionChangeRate();
        double holdImpulsivity = entity.getHoldImpulsivity();

        return 0.35 * spam
                + 0.25 * burst
                + 0.25 * direction
                + 0.15 * holdImpulsivity;
    }

    private double calculateInattention(GameMetricsEntity entity) {

        return 0.45 * (1.0 - entity.getAccuracy())
                + 0.35 * entity.getAttentionDecay()
                + 0.20 * entity.getRandomness();
    }

    // ===== EXTENDED BEHAVIORAL INDICES =====
    private double calculateADHDComposite(GameMetricsEntity entity) {

        return 0.5 * entity.getHyperactivity()
                + 0.5 * entity.getInattention();
    }

    private double calculateODDIndex(GameMetricsEntity entity) {

        return 0.30 * entity.getBurstIntensity()
                + 0.30 * entity.getDirectionChangeRate()
                + 0.25 * entity.getSpamIntensity()
                + 0.15 * (1.0 - entity.getAccuracy());
    }

    private double calculateConductIndex(GameMetricsEntity entity) {

        return 0.40 * (1.0 - entity.getAccuracy())
                + 0.35 * entity.getRandomness()
                + 0.25 * entity.getSpamIntensity();
    }

    private double calculateAnxietyIndex(GameMetricsEntity entity) {

        return 0.30 * entity.getAttentionDecay()
                + 0.25 * (1.0 - entity.getSpamIntensity())
                + 0.20 * (1.0 - entity.getBurstIntensity())
                + 0.25 * entity.getInattention();
    }

    private void logMetrics(GameMetricsEntity e) {
        System.out.println(
                "Saved metrics for childId = " + e.getChildId()
                + ", gameId = " + e.getGameId()
                + ", sessionId = " + e.getSessionId()
        );

        System.out.println("\n================ GAME METRICS ================");

        System.out.println("---- Base Metrics ----");
        System.out.println("Accuracy              : " + e.getAccuracy());
        System.out.println("Attention Decay       : " + e.getAttentionDecay());
        System.out.println("Randomness            : " + e.getRandomness());
        System.out.println("Burst Intensity       : " + e.getBurstIntensity());
        System.out.println("Spam Intensity        : " + e.getSpamIntensity());
        System.out.println("Direction Change Rate : " + e.getDirectionChangeRate());
        System.out.println("Hold Impulsivity      : " + e.getHoldImpulsivity());

        System.out.println("\n---- Core Indices ----");
        System.out.println("Hyperactivity         : " + e.getHyperactivity());
        System.out.println("Inattention           : " + e.getInattention());

        System.out.println("\n---- Extended Indices ----");
        System.out.println("ADHD Composite        : " + e.getAdhdComposite());
        System.out.println("ODD Index             : " + e.getOddIndex());
        System.out.println("Conduct Index         : " + e.getConductIndex());
        System.out.println("Anxiety Index         : " + e.getAnxietyIndex());

        System.out.println("Created At            : " + e.getCreatedAt());

        System.out.println("==============================================\n");
    }

}
