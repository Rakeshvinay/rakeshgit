package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class PasswordVerificationRequest {
    private String email;
    private String password;
} 