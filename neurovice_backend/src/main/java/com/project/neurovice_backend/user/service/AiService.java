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

    // This should point to the base URL of your FastAPI server (e.g., "http://127.0.0.1:8000")
    private final String aiServiceBaseUrl;

    public AiService(
            RestTemplate restTemplate,
            @Value("${ai.service.url}") String aiServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.aiServiceBaseUrl = aiServiceBaseUrl;
    }

    /**
     * Sends the 6 clinical threshold counts to the Questionnaire AI Model.
     */
    public Double getQuestRisk(Map<String, Object> questFeatures) {
        String endpoint = aiServiceBaseUrl + "/predict/questionnaire";
        return fetchRiskScore(endpoint, questFeatures, "questionnaire_risk_score");
    }

    /**
     * Sends the 7 base cognitive metrics to the Game AI Model.
     */
    public Double getGameRisk(Map<String, Object> gameFeatures) {
        String endpoint = aiServiceBaseUrl + "/predict/game";
        return fetchRiskScore(endpoint, gameFeatures, "game_risk_score");
    }

    /**
     * Private helper method to handle the actual HTTP communication.
     */
    private Double fetchRiskScore(String endpointUrl, Map<String, Object> payload, String expectedJsonKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(endpointUrl, request, Map.class);

            if (response.getBody() == null) {
                return null;
            }

            System.out.println("AI Service HTTP Status (" + endpointUrl + "): " + response.getStatusCode());
            System.out.println("AI Service Response: " + response.getBody());

            // Extracts the 0-100 clamped percentage score returned by FastAPI
            Object risk = response.getBody().get(expectedJsonKey);
            return risk == null ? null : ((Number) risk).doubleValue();

        } catch (Exception e) {
            System.err.println("Error communicating with AI Service at " + endpointUrl + ": " + e.getMessage());
            return null;
        }
    }
}
