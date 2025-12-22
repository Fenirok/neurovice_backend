package com.project.neurovice_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.neurovice_backend.dto.GameDataRequest;
import com.project.neurovice_backend.service.GameMetricsService;

@RestController
@RequestMapping("/api/game-data")
@CrossOrigin
public class GameDataController {

    private final GameMetricsService service;

    public GameDataController(GameMetricsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> receiveGameData(@RequestBody GameDataRequest data) {

        System.out.println(">>> CONTROLLER HIT <<<");

        // TEMP: just log data
        System.out.println("===== GAME DATA RECEIVED =====");
        System.out.println(data);
        System.out.println("==============================");

        // Minimal validation
        if (data.getTotalClicks() == null || data.getArrowPressCount() == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid game data payload");
        }

        service.processAndStore(data);

        return ResponseEntity.ok(
                Map.of("status", "stored")
        );
    }
}
