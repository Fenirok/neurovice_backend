package com.project.neurovice_backend.auth.email;

public class EmailTemplateUtil {

    public static String getOtpMessage(String otp) {
        return "Your OTP is: " + otp + "\nValid for 5 minutes.";
    }
}
