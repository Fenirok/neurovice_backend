package com.project.neurovice_backend.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.GameMetricsEntity;

@Repository
public interface GameMetricsRepository extends JpaRepository<GameMetricsEntity, Long> {

    List<GameMetricsEntity> findByChildIdOrderByCreatedAtAsc(Long childId);
}
