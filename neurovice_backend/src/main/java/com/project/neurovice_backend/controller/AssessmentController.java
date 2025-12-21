package com.project.neurovice_backend.controller;

import com.project.neurovice_backend.dto.*;
import com.project.neurovice_backend.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
public class AssessmentController {
    private final AssessmentService assessmentService;

    @PostMapping("/start")
    public ResponseEntity<StartAssessmentResponse> startAssessment(
            @RequestParam(required = false) Integer childId,
            @RequestBody(required = false) StartAssessmentRequest body) {
        Integer id = childId != null ? childId : (body != null ? body.getChildId() : null);
        if (id == null) {
            throw new com.project.neurovice_backend.exception.BadRequestException("childId is required");
        }
        return ResponseEntity.ok(assessmentService.getOrCreateAssessment(id));
    }

    @PostMapping("/{assessmentId}/section")
    public ResponseEntity<SectionSubmitResponse> submitSection(
            @PathVariable Integer assessmentId,
            @RequestBody SectionSubmitRequest request) {
        return ResponseEntity.ok(assessmentService.submitSection(assessmentId, request));
    }

    @GetMapping("/{assessmentId}/status")
    public ResponseEntity<AssessmentStatusResponse> getStatus(@PathVariable Integer assessmentId) {
        return ResponseEntity.ok(assessmentService.getStatus(assessmentId));
    }

    @GetMapping("/{assessmentId}/diagnosis")
    public ResponseEntity<DiagnosisResponse> getDiagnosis(@PathVariable Integer assessmentId) {
        return ResponseEntity.ok(assessmentService.getDiagnosis(assessmentId));
    }
    /* 
    @GetMapping("/{assessmentId}/ai-diagnosis")
    public AiDiagnosisResponse getAiDiagnosis(@PathVariable Integer assessmentId) {
        return ResponseEntity.ok(assessmentService.getAiDiagnosis(assessmentId));
    }*/
}
