package com.project.neurovice_backend.dto;

import lombok.Data;

@Data
public class StartAssessmentResponse {
    private Integer assessmentId;
    public StartAssessmentResponse(Integer assessmentId) {
        this.assessmentId = assessmentId;
    }
}
