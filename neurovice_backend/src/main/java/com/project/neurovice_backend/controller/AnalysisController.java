package com.project.neurovice_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.neurovice_backend.user.dto.AnalysisResponse;
import com.project.neurovice_backend.user.service.AnalysisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/run")
    public ResponseEntity<AnalysisResponse> run(@RequestParam Long childId) {
        System.out.println("Returning analysis response");
        return ResponseEntity.ok(analysisService.run(childId));
    }
}
