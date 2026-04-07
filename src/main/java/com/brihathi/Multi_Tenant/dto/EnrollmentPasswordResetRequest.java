package com.brihathi.Multi_Tenant.dto;
 
 
import lombok.Data;
 
@Data
public class EnrollmentPasswordResetRequest {
    private String enrollmentId;
        private String newPassword;
}
 