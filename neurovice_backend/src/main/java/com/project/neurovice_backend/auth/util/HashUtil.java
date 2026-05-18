package com.project.neurovice_backend.auth.util;

import java.security.MessageDigest;

public class HashUtil {

    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte hash[] = md.digest(input.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed");
        }
    }

    public static boolean verify(String input, String hashed) {
        return hash(input).equals(hashed);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
