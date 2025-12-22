package com.project.neurovice_backend.dto;

import lombok.Data;

@Data
public class SectionSubmitResponse {
    private boolean sectionCompleted;
    private boolean allSectionsCompleted;

    public SectionSubmitResponse(boolean sectionCompleted, boolean allSectionsCompleted) {
        this.sectionCompleted = sectionCompleted;
        this.allSectionsCompleted = allSectionsCompleted;
    }
}
