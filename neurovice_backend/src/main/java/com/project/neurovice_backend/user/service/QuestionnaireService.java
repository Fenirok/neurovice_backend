package com.project.neurovice_backend.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.neurovice_backend.user.model.questionaires;
import com.project.neurovice_backend.user.repository.questionairesRepository;

import lombok.RequiredArgsConstructor;

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
