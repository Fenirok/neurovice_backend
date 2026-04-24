package com.project.neurovice_backend.auth.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OtpEntity {

    private String email;
    private String hashedOtp;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int attempts;
    private boolean isUsed;

}
