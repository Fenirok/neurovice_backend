package com.project.neurovice_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.neurovice_backend.model.questionaires;
import com.project.neurovice_backend.service.QuestionnaireService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questionaires")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @GetMapping("/getallquestionaires")
    //@GetMapping("/getallquestionnaires")
    public List<questionaires> getAll() {
        return questionnaireService.getActiveSections();
    }
}
