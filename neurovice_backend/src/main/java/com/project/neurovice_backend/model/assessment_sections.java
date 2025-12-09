package com.project.neurovice_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

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
