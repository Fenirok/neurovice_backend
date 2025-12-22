package com.project.neurovice_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "game_metrics")
@Data
public class GameMetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // IDENTIFIERS
    private Long childId;
    private String gameId;
    private String sessionId;

    // Raw metrics
    private Integer totalClicks;
    private Integer burstCount;
    private Integer arrowPressCount;
    private Double arrowAvgInterval;
    private Double arrowIntervalSum;
    private Double arrowSpamRate;
    private Integer directionChangeCount;
    private Double totalArrowHoldTime;

    private Integer score0to60;
    private Integer score60to120;
    private Integer score120to180;

    private Integer totalTargetsAppeared;
    private Integer totalTargetsCaught;
    private Integer timeLeft;

    // Base metrics
    private Double accuracy;
    private Double attentionDecay;
    private Double randomness;
    private Double burstIntensity;
    private Double spamIntensity;
    private Double directionChangeRate;
    private Double holdImpulsivity;

    // Core indices
    private Double hyperactivity;
    private Double inattention;

    // Extended behavioral indices
    private Double adhdComposite;
    private Double oddIndex;
    private Double conductIndex;
    private Double anxietyIndex;

    private LocalDateTime createdAt;

    // getters & setters
}
