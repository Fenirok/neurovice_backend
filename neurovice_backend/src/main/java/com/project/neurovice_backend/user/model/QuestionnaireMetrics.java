package com.project.neurovice_backend.user.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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
