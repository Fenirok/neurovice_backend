package com.project.neurovice_backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.assessment;

@Repository
public interface assessmentRepository extends JpaRepository<assessment, Long> {

    Optional<assessment> findByChildIdAndStatusIn(Integer childId, List<String> statuses);

    Optional<assessment> findTop1ByChildIdAndStatusInOrderByCreatedAtDesc(Integer childId, List<String> statuses);

    // Add custom query methods if needed
    @Query("""
        SELECT a.id
        FROM assessment a
        WHERE a.childId = :childId
        ORDER BY a.createdAt DESC
    """)
    Integer findLatestAssessmentIdByChildId(@Param("childId") Long childId);

}
