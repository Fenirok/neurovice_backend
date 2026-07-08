package com.project.neurovice_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long parentId;
    private String username;
    private String email;
}
