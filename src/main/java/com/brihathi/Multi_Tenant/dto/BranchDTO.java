package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.dto.*;
import java.util.List;


import lombok.Data;

@Data
public class BranchDTO {

    private Long branchId;
    private String branchName;
    private List<BatchDTO> batches;

}
