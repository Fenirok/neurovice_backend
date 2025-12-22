package com.project.neurovice_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.neurovice_backend.model.assessment_sections;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface assessmentSectionsRepository extends JpaRepository<assessment_sections, Long> {
    @Query(value = "SELECT COUNT(DISTINCT section_id) FROM assessment_sections WHERE assessment_id = :assessmentId", nativeQuery = true)
    int countDistinctSectionsByAssessmentId(@Param("assessmentId") Integer assessmentId);

    @Query(value = "SELECT section_id FROM assessment_sections WHERE assessment_id = :assessmentId", nativeQuery = true)
    List<String> findSectionIdsByAssessmentId(@Param("assessmentId") Integer assessmentId);

    @Query(value = "SELECT * FROM assessment_sections WHERE assessment_id = :assessmentId", nativeQuery = true)
    List<assessment_sections> findByAssessmentId(@Param("assessmentId") Integer assessmentId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM assessment_sections WHERE assessment_id = :assessmentId AND section_id = :sectionId)", nativeQuery = true)
    boolean existsByAssessmentIdAndSectionId(@Param("assessmentId") Integer assessmentId, @Param("sectionId") String sectionId);
}
