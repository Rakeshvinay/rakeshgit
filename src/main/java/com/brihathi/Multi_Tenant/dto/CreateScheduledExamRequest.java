package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateScheduledExamRequest {
    private Long educatorId;

    // Exam scope
    private Subject subject;
         // ALL / PHYSICS / CHEMISTRY / BIOLOGY
    private Difficulty difficulty;   // BASIC / MEDIUM / HARD
    private String grade;             // 11th / 12th

    // Target audience
    private String branch;            // Mumbai
    private String batch;             // IIT1

    // Scheduling
    private LocalDate scheduledDate;
    private LocalDateTime scheduledTime;
    private LocalDateTime examEndTime;
}
