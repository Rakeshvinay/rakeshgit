
package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.dto.OtpSendRequest;
import com.brihathi.Multi_Tenant.dto.OtpVerifyRequest;
import com.brihathi.Multi_Tenant.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    // Send OTP
    @PostMapping("/send")
    public String sendOtp(@RequestBody OtpSendRequest request) {
        otpService.sendOtp(request.getPhoneNumber());
        return "OTP sent successfully!";
    }

    // Verify OTP
    @PostMapping("/verify")
    public String verifyOtp(@RequestBody OtpVerifyRequest request) {
        boolean valid = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        return valid ? "OTP verified!" : "Invalid or expired OTP.";
    }
}

