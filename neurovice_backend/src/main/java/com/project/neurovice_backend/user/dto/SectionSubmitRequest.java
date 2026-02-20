package com.project.neurovice_backend.user.dto;

import java.util.Map;

import lombok.Data;

@Data
public class SectionSubmitRequest {

    private String sectionId;
    private Map<String, Integer> answers;
}
