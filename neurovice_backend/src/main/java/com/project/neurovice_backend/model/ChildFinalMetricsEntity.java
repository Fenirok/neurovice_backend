package com.project.neurovice_backend.model;

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
        name = "child_final_metrics",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "child_id")
        }
)
@Data
public class ChildFinalMetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One row per child
    @Column(name = "child_id", nullable = false)
    private Long childId;

    // Optional but useful
    @Column(name = "game_id")
    private String gameId;

    // How many sessions were used to compute this
    @Column(name = "session_count")
    private Integer sessionCount;

    // ---------- FINAL VALUES  ----------
    private Double hyperactivity;
    private Double inattention;
    private Double adhdComposite;
    private Double oddIndex;
    private Double conductIndex;
    private Double anxietyIndex;

    // Metadata
    @Column(name = "computed_at")
    private LocalDateTime computedAt;
}
