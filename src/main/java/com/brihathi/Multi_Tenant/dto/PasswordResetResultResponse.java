package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.Educator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResultResponse {
    private String message;
    private Educator educator;
} 