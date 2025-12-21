package com.project.neurovice_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@Service
public class AiService {

    private static final String AI_URL = "http://localhost:8000/predict";
    private final RestTemplate restTemplate = new RestTemplate();

    public Double getAdhdRisk(Map<String, Double> aiFeatures) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Double>> request =
                new HttpEntity<>(aiFeatures, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(AI_URL, request, Map.class);

        return ((Number) response.getBody()
                .get("adhd_risk_percent")).doubleValue();
    }
}
