package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetEmailResponse {
    private String message;
    private String otp;
    private String token;
    private Long expiresIn;
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
} 