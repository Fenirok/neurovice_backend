package com.project.neurovice_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.QuestionnaireMetrics;

@Repository
public interface QuestionnaireMetricsRepository extends JpaRepository<QuestionnaireMetrics, Long> {

    Optional<QuestionnaireMetrics> findByAssessmentId(Integer assessmentId);
}
