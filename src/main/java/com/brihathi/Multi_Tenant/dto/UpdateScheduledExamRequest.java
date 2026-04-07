package com.brihathi.Multi_Tenant.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateScheduledExamRequest {

    private LocalDate scheduledDate;
    private LocalDateTime scheduledTime;
}
