package com.project.neurovice_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.ADHDAnalysisResult;

@Repository
public interface ADHDAnalysisRepository extends JpaRepository<ADHDAnalysisResult, Long> {

    /**
     * Fetches the absolute latest analysis result for a specific child. We use
     * this to grab the previous_final_risk and raw_game_score to feed into the
     * RiskFusionService for temporal smoothing.
     */
    Optional<ADHDAnalysisResult> findFirstByChildIdOrderByLastUpdatedDesc(Long childId);
}
