package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class EmailPasswordResetRequest {
    private String email;
    private String newPassword;
} 