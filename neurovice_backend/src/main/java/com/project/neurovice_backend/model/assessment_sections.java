package com.project.neurovice_backend.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.Map;


@Entity
@Table(name = "assessment_sections")
@Data
public class assessment_sections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assessment_section_id;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    @Column(name = "assessment_id", nullable = false)
    private String assessmentID;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @Column(name = "section_id", nullable = false)
    private String sectionID;


    @Column(name = "disorder_type", nullable = false)
    private String disorderType;

    @Column(name = "sum_score", nullable = false)
    private Integer sumScore;

    @Column(name = "avg_score", nullable = false)
    private Double avgScore;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "jsonb", name = "question_response")
    private Map<String, Object> questionResponse;

}
