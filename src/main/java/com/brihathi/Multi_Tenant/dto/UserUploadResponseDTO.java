package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUploadResponseDTO {
    private int total;
    private int success;
    private int failed;
    private String message;
}