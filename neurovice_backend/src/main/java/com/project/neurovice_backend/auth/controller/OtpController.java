package com.project.neurovice_backend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.neurovice_backend.auth.dto.OtpRequests;
import com.project.neurovice_backend.auth.dto.OtpVerify;
import com.project.neurovice_backend.auth.service.OtpService;

@RestController
@RequestMapping("/api/auth/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // request otp
    @PostMapping("/request")
    public ResponseEntity<String> requestOtp(@RequestBody OtpRequests request) {
        otpService.requestOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent sucessfully");
    }

    // verify otp
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerify request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }

}
