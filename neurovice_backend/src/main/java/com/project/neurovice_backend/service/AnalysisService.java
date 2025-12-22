package com.project.neurovice_backend.service;

import com.project.neurovice_backend.dto.AnalysisResponse;
import com.project.neurovice_backend.exception.NotFoundException;
import com.project.neurovice_backend.model.ChildFinalMetricsEntity;
import com.project.neurovice_backend.repository.ChildFinalMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ChildFinalMetricsRepository childFinalMetricsRepository;
    private final AiService aiService;

    public AnalysisResponse run(Long childId) {
        ChildFinalMetricsEntity metrics = childFinalMetricsRepository
                .findByChildId(childId)
                .orElseThrow(() -> new NotFoundException("Final metrics not found for child"));

        Map<String, Object> features = new HashMap<>();
        features.put("adhd_composite", nz(metrics.getAdhdComposite()));
        features.put("inattention", nz(metrics.getInattention()));
        features.put("hyperactivity", nz(metrics.getHyperactivity()));
        features.put("anxiety_index", nz(metrics.getAnxietyIndex()));
        features.put("conduct_index", nz(metrics.getConductIndex()));
        features.put("odd_index", nz(metrics.getOddIndex()));
        features.put("session_count", metrics.getSessionCount());

        Double risk = aiService.getAdhdRisk(features);
        double r = risk == null ? 0.0 : risk;
        return new AnalysisResponse(r);
    }

    private double nz(Double v) {
        return v == null ? 0.0 : v;
    }
}
