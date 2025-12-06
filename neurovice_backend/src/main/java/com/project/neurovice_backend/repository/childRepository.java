package com.project.neurovice_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.neurovice_backend.model.child;
import org.springframework.stereotype.Repository;

@Repository
public interface childRepository extends JpaRepository<child, Long> {
    // Add custom query methods if needed
}
