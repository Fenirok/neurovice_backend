package com.project.neurovice_backend.user.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CreateChildRequest {

    private Long parentId;
    private String childName;
    private LocalDate dateOfBirth;
    private String gender;
    private String aadharId;
}
