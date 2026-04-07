package com.brihathi.Multi_Tenant.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserExcelDTO {
    private String name;
    private String enrollmentId;
    private String password;
    private Long phoneNumber;
    private LocalDate dateOfBirth;
    private String grade;
    private String state;
    private String city;
    private String branch;
    private String batch;
    private String profileImage;
    private String lastLogin;
    private String currentToken;
    private String parentName;
    private Long parentPhone;
    private Long tenantId;
}
