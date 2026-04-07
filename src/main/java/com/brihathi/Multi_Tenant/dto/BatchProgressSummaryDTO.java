package com.brihathi.Multi_Tenant.dto;
 
import lombok.*;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchProgressSummaryDTO {
 
    private String branch;
    private String batch;
    private Long totalStudents;
    private Double avgMarks;
    private Double accuracy;
    private Long testsConducted;
}
 