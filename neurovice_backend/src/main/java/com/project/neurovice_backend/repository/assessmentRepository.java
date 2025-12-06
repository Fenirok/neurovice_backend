package com.project.neurovice_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.neurovice_backend.model.assessment;
import org.springframework.stereotype.Repository;

@Repository
public interface assessmentRepository extends JpaRepository<assessment, Long> {
    // Add custom query methods if needed
}
