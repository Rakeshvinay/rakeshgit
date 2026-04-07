package com.brihathi.Multi_Tenant.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchSubjectPerformanceDTO {

    private String subject;
    private Double totalPercentage;
    private Long countRows;
    private Double averagePercentage;
}
