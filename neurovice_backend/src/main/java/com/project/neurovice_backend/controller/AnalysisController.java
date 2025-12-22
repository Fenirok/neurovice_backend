package com.project.neurovice_backend.controller;

import com.project.neurovice_backend.dto.AnalysisResponse;
import com.project.neurovice_backend.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/run")
    public ResponseEntity<AnalysisResponse> run(@RequestParam Long childId) {
        return ResponseEntity.ok(analysisService.run(childId));
    }
}
