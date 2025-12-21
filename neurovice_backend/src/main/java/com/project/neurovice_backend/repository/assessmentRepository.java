package com.project.neurovice_backend.repository;

import com.project.neurovice_backend.model.assessment;
import org.springframework.data.jpa.repository.JpaRepository;
    
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface assessmentRepository extends JpaRepository<assessment, Long> {
    Optional<assessment> findByChildIdAndStatusIn(Integer childId, List<String> statuses);
    Optional<assessment> findTop1ByChildIdAndStatusInOrderByCreatedAtDesc(Integer childId, List<String> statuses);
}
