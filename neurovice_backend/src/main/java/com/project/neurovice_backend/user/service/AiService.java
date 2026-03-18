package com.project.neurovice_backend.user.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public AiService(
            RestTemplate restTemplate,
            @Value("${ai.service.url}") String aiServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.aiServiceUrl = aiServiceUrl;
    }

    public Double getAdhdRisk(Map<String, Object> aiFeatures) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request
                = new HttpEntity<>(aiFeatures, headers);

        ResponseEntity<Map> response
                = restTemplate.postForEntity(aiServiceUrl, request, Map.class);

        if (response.getBody() == null) {
            return null;
        }

        System.out.println("AI Service HTTP Status: " + response.getStatusCode());
        System.out.println("AI Service Response: " + response.getBody());

        Object risk = response.getBody().get("adhd_risk_score");
        return risk == null ? null : ((Number) risk).doubleValue();
    }
}
