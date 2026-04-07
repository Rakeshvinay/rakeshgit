package com.brihathi.Multi_Tenant.dto;
import java.util.List;
import lombok.Data;

@Data
public class TenantDTO {

    private Long tenantId;
    private String tenantName;
    private List<BranchDTO> branches;

}
