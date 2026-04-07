package com.brihathi.Multi_Tenant.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDateRangeRequest {
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
} 