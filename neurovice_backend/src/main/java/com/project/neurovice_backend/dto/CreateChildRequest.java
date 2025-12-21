package com.project.neurovice_backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateChildRequest {
    private String childName;
    private LocalDate dateOfBirth;
    private String gender;
    private String aadharId;
}
