package com.project.neurovice_backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.ADHDRawGameMetrics;

@Repository
public interface GameMetricsRepository extends JpaRepository<ADHDRawGameMetrics, Long> {

    Optional<ADHDRawGameMetrics> findByChildId(Long childId);

    List<ADHDRawGameMetrics> findByChildIdOrderByCreatedAtAsc(Long childId);
}
