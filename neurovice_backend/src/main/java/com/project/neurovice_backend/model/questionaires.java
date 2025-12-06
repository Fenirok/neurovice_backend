package com.project.neurovice_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Column;
import java.util.Map;

@Entity
@Table(name = "questionaires")
@Data
public class questionaires{

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_id", nullable = false, unique = true)
    private String sectionId;

    @Column(name = "section_name")
    private String sectionName;

    private String description;

    @Column(name = "disorder_type", nullable = false)
    private String disorderType;

    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> questions;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
