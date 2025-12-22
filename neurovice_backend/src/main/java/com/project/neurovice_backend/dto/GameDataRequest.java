package com.project.neurovice_backend.dto;

import lombok.Data;

@Data
public class GameDataRequest {

    private Integer totalClicks;
    private Integer burstCount;

    private Integer arrowPressCount;
    private Double arrowAvgInterval;
    private Double arrowIntervalSum;
    private Double arrowSpamRate;
    private Integer directionChangeCount;
    private Double totalArrowHoldTime;

    private Integer score_0_60;
    private Integer score_60_120;
    private Integer score_120_180;

    private Integer totalTargetsAppeared;
    private Integer totalTargetsCaught;

    private Integer timeLeft;
}
