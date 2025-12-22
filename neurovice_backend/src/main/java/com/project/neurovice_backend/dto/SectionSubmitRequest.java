package com.project.neurovice_backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SectionSubmitRequest {
    private String sectionId;
    private Map<String, Integer> answers;
}
