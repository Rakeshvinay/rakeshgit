package com.brihathi.Multi_Tenant.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectPerformanceDTO {

    private String subject;
    private Long studentCount;
    private Double averageMarks;
    private Double averagePercentage;
    private String performance;
}
