package com.project.neurovice_backend.repository;

import com.project.neurovice_backend.model.QuestionnaireMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QuestionnaireMetricsRepository extends JpaRepository<QuestionnaireMetrics, Long> {
    Optional<QuestionnaireMetrics> findByAssessmentId(Integer assessmentId);
}
