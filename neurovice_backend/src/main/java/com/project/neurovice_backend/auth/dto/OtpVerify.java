package com.project.neurovice_backend.auth.dto;

import lombok.Data;

@Data
public class OtpVerify {

    private String email;
    private String otp;
}
