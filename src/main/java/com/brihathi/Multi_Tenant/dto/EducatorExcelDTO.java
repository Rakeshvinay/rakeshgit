package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class EducatorExcelDTO {
    private String educatorName;
    private String email;
    private String password;
    private Long phoneNumber;
    private String subject;
    private Long tenantId;
}
