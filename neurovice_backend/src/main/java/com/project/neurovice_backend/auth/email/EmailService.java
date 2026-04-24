package com.project.neurovice_backend.auth.email;

public interface EmailService {

    void sendOtp(String to, String otp);
}
