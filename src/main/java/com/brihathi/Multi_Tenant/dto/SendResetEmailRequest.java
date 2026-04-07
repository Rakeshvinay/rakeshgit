package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class SendResetEmailRequest {
    private String input; // Can be email or phone number
} 