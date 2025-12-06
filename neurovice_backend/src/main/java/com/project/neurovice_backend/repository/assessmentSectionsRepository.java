package com.project.neurovice_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.neurovice_backend.model.assessment_sections;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface assessmentSectionsRepository extends JpaRepository<assessment_sections, Long> {
    @Query("SELECT COUNT(DISTINCT a.sectionId) FROM assessment_sections a WHERE a.assessmentId = :assessmentId")
    int countDistinctSectionsByAssessmentId(@Param("assessmentId") Integer assessmentId);

    @Query("SELECT a.sectionId FROM assessment_sections a WHERE a.assessmentId = :assessmentId")
    List<String> findSectionIdsByAssessmentId(@Param("assessmentId") Integer assessmentId);

    boolean existsByAssessmentIdAndSectionId(Integer assessmentId, String sectionId);
}
