package com.project.neurovice_backend.user.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String whatsappNumber;
    private String address;
    private String aadhaarId;
}
