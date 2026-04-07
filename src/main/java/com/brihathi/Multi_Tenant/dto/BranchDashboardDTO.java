package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDashboardDTO {

    private String branchId;
    private String branchName;
    private Long totalBatches;
    private Long totalStudents;
    private List<BatchDashboardDTO> batches;

     // ✅ ADD THIS
     private List<BranchSubjectPerformanceDTO> subjectPerformance;

     
    // ✅ NEW FIELD
    private Double branchAveragePercentage;
}
