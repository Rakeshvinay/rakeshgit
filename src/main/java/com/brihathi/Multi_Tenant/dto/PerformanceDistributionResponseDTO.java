package com.brihathi.Multi_Tenant.dto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDistributionResponseDTO {

    private Long totalStudents;          // card 1
    private Double averagePercentage;    // card 2

    private List<PerformanceDistributionDTO> distribution;  // existing chart data
}

