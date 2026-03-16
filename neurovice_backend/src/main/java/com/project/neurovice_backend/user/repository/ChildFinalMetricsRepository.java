package com.project.neurovice_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.ADHDFinalMetrics;

@Repository
public interface ChildFinalMetricsRepository
        extends JpaRepository<ADHDFinalMetrics, Long> {

    // Fetch final metrics for a child (there will be at most ONE row)
    Optional<ADHDFinalMetrics> findByChildId(Long childId);
}
