package com.brihathi.Multi_Tenant.dto;
 
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder   // ✅ ADD THIS
@AllArgsConstructor
@NoArgsConstructor
public class BatchAIInsightDTO {
 
    private Long tenantId;
    private String branch;
    private String batch;
 
    private Long userId;
    private String name;
    private String enrollmentId;
 
    private String reason;
    private String details;

    private String recommendedAction;
}
 
 