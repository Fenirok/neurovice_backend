package com.project.neurovice_backend.dto;

import lombok.Data;

@Data
public class DiagnosisResponse {
    private boolean inattention;
    private boolean hyperactivity;
    private boolean combined;
    private boolean odd;
    private boolean conduct;
    private boolean anxietyDepression;

    public DiagnosisResponse(boolean inattention, boolean hyperactivity, boolean combined, 
                           boolean odd, boolean conduct, boolean anxietyDepression) {
        this.inattention = inattention;
        this.hyperactivity = hyperactivity;
        this.combined = combined;
        this.odd = odd;
        this.conduct = conduct;
        this.anxietyDepression = anxietyDepression;
    }
}
