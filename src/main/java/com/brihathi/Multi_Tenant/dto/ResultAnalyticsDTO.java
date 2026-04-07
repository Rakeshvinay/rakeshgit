package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ResultAnalyticsDTO {
    private Integer correct;
    private Integer wrong;
    private Integer skipped;
}
