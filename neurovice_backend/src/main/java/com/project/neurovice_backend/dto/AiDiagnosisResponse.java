package com.project.neurovice_backend.dto;

import lombok.Data;

@Data
public class AiDiagnosisResponse {
    private double adhdRiskPercent;
    private String riskLevel; // LOW / MEDIUM / HIGH
}
