package com.brihathi.Multi_Tenant.dto;
 
import lombok.Data;
 
@Data
public class PasswordVerificationStudentRequest {
    private String enrollmentId;
    private String password;
}