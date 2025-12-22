package com.project.neurovice_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssessmentStatusResponse {
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<String> submittedSections;
}
