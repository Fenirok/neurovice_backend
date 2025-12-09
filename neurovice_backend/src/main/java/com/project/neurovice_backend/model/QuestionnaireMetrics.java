package com.project.neurovice_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "questionnaire_metrics")
@Data
public class QuestionnaireMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assessment_id", nullable = false)
    private Integer assessmentId;

    @Column(name = "child_id", nullable = false)
    private Integer childId;

    @Column(name = "inattention")
    private Integer inattention = 0;

    @Column(name = "hyperactivity")
    private Integer hyperactivity = 0;

    @Column(name = "combined")
    private Integer combined = 0;

    @Column(name = "odd")
    private Integer odd = 0;

    @Column(name = "conduct")
    private Integer conduct = 0;

    @Column(name = "anxiety_depression")
    private Integer anxietyDepression = 0;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
