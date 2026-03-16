package com.project.neurovice_backend.user.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "assessment_sections")
@Data
public class assessment_sections {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_section_id")
    private Long assessmentSectionId;

    @Column(name = "assessment_id", nullable = false)
    private Integer assessmentId;

    @Column(name = "section_id", nullable = false)
    private String sectionId;

    @Column(name = "disorder_type", nullable = false)
    private String disorderType;

    @Column(name = "sum_score", nullable = false)
    private Integer sumScore;

    @Column(name = "avg_score", nullable = false)
    private Double avgScore;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "question_response")
    private Map<String, Object> questionResponse;
}
