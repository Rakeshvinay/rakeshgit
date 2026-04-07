package com.brihathi.Multi_Tenant.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IndividualSubjectwiseRequest {
    private Long userId;
    private String difficultyLevel;
    private String subject;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
} 