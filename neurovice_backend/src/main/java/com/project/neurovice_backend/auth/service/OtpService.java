package com.project.neurovice_backend.auth.service;

public interface OtpService {

    void requestOtp(String email);

    boolean verifyOtp(String email, String otp);
}
