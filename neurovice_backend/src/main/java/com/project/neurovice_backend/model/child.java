package com.project.neurovice_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity
@Table(name = "child")
@Data
public class child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long childId;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Column(name = "child_name", nullable = false)
    private String childName;
    
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(name = "aadhar_id", nullable = false, unique = true)
    private String aadharId;
}
