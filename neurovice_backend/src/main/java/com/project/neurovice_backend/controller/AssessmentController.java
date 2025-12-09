package com.project.neurovice_backend.controller;

import com.project.neurovice_backend.dto.*;
import com.project.neurovice_backend.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessment")
@RequiredArgsConstructor
public class AssessmentController {
    private final AssessmentService assessmentService;

    @PostMapping("/start")
    public StartAssessmentResponse start(@RequestBody StartAssessmentRequest request) {
        return assessmentService.startAssessment(request.getChildId());
    }

    @PostMapping("/{assessmentId}/section")
    public SectionSubmitResponse submitSection(
            @PathVariable Integer assessmentId,
            @RequestBody SectionSubmitRequest request) {
        return assessmentService.submitSection(assessmentId, request);
    }

    @GetMapping("/{assessmentId}/status")
    public AssessmentStatusResponse getStatus(@PathVariable Integer assessmentId) {
        return assessmentService.getStatus(assessmentId);
    }

    @GetMapping("/{assessmentId}/diagnosis")
    public DiagnosisResponse getDiagnosis(@PathVariable Integer assessmentId) {
        return assessmentService.getDiagnosis(assessmentId);
    }
}
