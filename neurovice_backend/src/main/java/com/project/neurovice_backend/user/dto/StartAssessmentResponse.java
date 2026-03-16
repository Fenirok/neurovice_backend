package com.project.neurovice_backend.user.dto;

import lombok.Data;

@Data
public class StartAssessmentResponse {

    private Integer assessmentId;

    public StartAssessmentResponse(Integer assessmentId) {
        this.assessmentId = assessmentId;
    }
}
