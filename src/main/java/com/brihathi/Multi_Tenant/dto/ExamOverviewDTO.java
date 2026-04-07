package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamOverviewDTO {
    private Long total;
    private Long upcoming;
    private Long completed;
    private Long cancelled;
}

