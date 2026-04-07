package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StartScheduledExamResponse {

    private Long eduScheduledExamId;
    private Long scheduledExamId;
    private Long userId;

    private LocalDate scheduledDate;
    private LocalDateTime scheduledTime;

    private Long tenantId;
    private String batch;
    private String branch;

    private String subjectId;
    private Subject examType;
    private Difficulty difficulty;
    private String grade;

    private Integer totalMarks;
    private String totalDuration;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
