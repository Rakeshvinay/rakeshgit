package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDashboardDTO {

    private String batchId;
    private String batchName;
    private Long studentCount;
}
