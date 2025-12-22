package com.project.neurovice_backend.controller;

import com.project.neurovice_backend.model.questionaires;
import com.project.neurovice_backend.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/questionnaires")
@RequiredArgsConstructor
public class QuestionnaireController {
    private final QuestionnaireService questionnaireService;

    @GetMapping("/getallquestionnaires")
    public List<questionaires> getAll() {
        return questionnaireService.getActiveSections();
    }
}
