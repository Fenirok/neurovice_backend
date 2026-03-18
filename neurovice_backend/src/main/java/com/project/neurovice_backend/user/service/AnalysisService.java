package com.project.neurovice_backend.user.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.neurovice_backend.user.dto.AnalysisResponse;
import com.project.neurovice_backend.user.exception.NotFoundException;
import com.project.neurovice_backend.user.model.ADHDAnalysisResult;
import com.project.neurovice_backend.user.model.ADHDFinalMetrics;
import com.project.neurovice_backend.user.repository.ADHDAnalysisRepository;
import com.project.neurovice_backend.user.repository.ChildFinalMetricsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ChildFinalMetricsRepository childFinalMetricsRepository;
    private final AiService aiService;
    private final ADHDAnalysisRepository adhdAnalysisRepository;

    public AnalysisResponse run(Long childId) {
        ADHDFinalMetrics metrics = childFinalMetricsRepository
                .findByChildId(childId)
                .orElseThrow(() -> new NotFoundException("Final metrics not found for child"));

        Map<String, Object> features = new HashMap<>();
        features.put("adhd_composite", nz(metrics.getAdhdComposite()));
        features.put("inattention", nz(metrics.getInattention()));
        features.put("hyperactivity", nz(metrics.getHyperactivity()));
        features.put("anxiety_index", nz(metrics.getAnxietyIndex()));
        features.put("conduct_index", nz(metrics.getConductIndex()));
        features.put("odd_index", nz(metrics.getOddIndex()));
        // features.put("session_count", metrics.getSessionCount());

        Double risk = aiService.getAdhdRisk(features);
        System.out.println("Risk returned from AI service: " + risk);
        double r = risk == null ? 0.0 : risk;

        ADHDAnalysisResult result = new ADHDAnalysisResult();
        result.setChildId(childId);
        result.setScore(r);
        result.setLastUpdated(LocalDateTime.now());

        adhdAnalysisRepository.save(result);
        System.out.println("Saved analysis result to DB");

        return new AnalysisResponse(r);
    }

    private double nz(Double v) {
        return v == null ? 0.0 : v;
    }
}
