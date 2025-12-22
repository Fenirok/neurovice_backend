package com.project.neurovice_backend.controller;

import com.project.neurovice_backend.model.questionaires;
import com.project.neurovice_backend.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
