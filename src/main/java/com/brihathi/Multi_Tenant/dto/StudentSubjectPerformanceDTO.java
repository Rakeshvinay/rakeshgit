package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSubjectPerformanceDTO {

    private Double totalPercentage;
    private Long countRows;
    private Double averagePercentage;
    private String subject;
    private String description;
}
