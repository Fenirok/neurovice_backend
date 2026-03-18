package com.project.neurovice_backend.user.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(
        name = "adhd_analysis_result",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "child_id")
        }
)

@Data
public class ADHDAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "child_id", nullable = false)
    private Long childId;

    @Column(name = "score")
    private Double score;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

}
