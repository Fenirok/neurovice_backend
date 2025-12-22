package com.project.neurovice_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.model.ChildFinalMetricsEntity;

@Repository
public interface ChildFinalMetricsRepository
        extends JpaRepository<ChildFinalMetricsEntity, Long> {

    // Fetch final metrics for a child (there will be at most ONE row)
    Optional<ChildFinalMetricsEntity> findByChildId(Long childId);
}
