package com.project.neurovice_backend.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.neurovice_backend.auth.email.EmailService;
import com.project.neurovice_backend.auth.entity.OtpEntity;
import com.project.neurovice_backend.auth.repository.OtpRepository;
import com.project.neurovice_backend.auth.util.HashUtil;
import com.project.neurovice_backend.auth.util.OtpGenerator;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpServiceImpl(OtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    @Override
    public void requestOtp(String email) {
        // invalidate existing OTPs for the email
        otpRepository.invalidateExisting(email);

        // generate new OTP
        String otp = OtpGenerator.generateOtp(email);

        // hash otp
        String hashedOtp = HashUtil.hash(otp);

        // create OTP entity
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setHashedOtp(hashedOtp);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setAttempts(0);
        otpEntity.setUsed(false);

        // save to db 
        otpRepository.save(otpEntity);

        // send OTP via email
        emailService.sendOtp(email, otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        Optional<OtpEntity> optional = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (optional.isEmpty()) {
            return false;
        }

        OtpEntity otpEntity = optional.get();

        if (otpEntity.isUsed() == true) {
            return false;
        }

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (otpEntity.getAttempts() >= 3) {
            return false;
        }

        boolean match = HashUtil.verify(otp, otpEntity.getHashedOtp());

        if (!match) {
            otpEntity.setAttempts((otpEntity.getAttempts() + 1));
            otpRepository.save(otpEntity);
            return false;
        }

        // mark as used
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
        return true;
    }
}
