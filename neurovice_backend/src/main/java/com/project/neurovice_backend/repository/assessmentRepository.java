package com.project.neurovice_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.model.assessment;

@Repository
public interface assessmentRepository extends JpaRepository<assessment, Long> {

    // Add custom query methods if needed
    @Query("""
        SELECT a.id
        FROM assessment a
        WHERE a.childId = :childId
        ORDER BY a.createdAt DESC
    """)
    Integer findLatestAssessmentIdByChildId(@Param("childId") Long childId);
}
