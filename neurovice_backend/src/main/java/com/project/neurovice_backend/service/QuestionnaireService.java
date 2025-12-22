package com.project.neurovice_backend.service;

import com.project.neurovice_backend.model.questionaires;
import com.project.neurovice_backend.repository.questionairesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {
    private final questionairesRepository questionnaireRepository;

    public List<questionaires> getActiveSections() {
        return questionnaireRepository.findAllByIsActiveTrueOrderByDisplayOrder();
    }

    public int countActiveSections() {
        return questionnaireRepository.findAllByIsActiveTrueOrderByDisplayOrder().size();
    }
}
