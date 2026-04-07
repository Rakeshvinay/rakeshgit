package com.brihathi.Multi_Tenant.service;

public interface OtpService {
    void sendOtp(String phoneNumber);
    boolean verifyOtp(String phoneNumber, String otp);
}
