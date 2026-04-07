package com.brihathi.Multi_Tenant.dto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerformanceDistributionDTO {

    private String range;
    private Long studentCount;
    private Double percentage; // out of total students
}
