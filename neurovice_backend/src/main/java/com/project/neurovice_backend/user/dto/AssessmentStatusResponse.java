package com.project.neurovice_backend.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class AssessmentStatusResponse {

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<String> submittedSections;
}
