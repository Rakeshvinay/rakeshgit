package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDashboardDTO {
    private String tenantName;
    private List<BranchDashboardDTO> branches;
}
