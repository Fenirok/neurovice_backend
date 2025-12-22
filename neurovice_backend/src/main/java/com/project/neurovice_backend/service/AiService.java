package com.project.neurovice_backend.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    private static final String AI_URL = "https://neurovice-ml.onrender.com/predict";
    private final RestTemplate restTemplate = new RestTemplate();

    public Double getAdhdRisk(Map<String, Object> aiFeatures) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request
                = new HttpEntity<>(aiFeatures, headers);

        ResponseEntity<Map> response
                = restTemplate.postForEntity(AI_URL, request, Map.class);

        Object v = response.getBody().get("risk");
        if (v == null) {
            v = response.getBody().get("adhd_risk");
        }
        if (v == null) {
            v = response.getBody().get("adhd_risk_percent");
        }
        return v == null ? null : ((Number) v).doubleValue();
    }
}
