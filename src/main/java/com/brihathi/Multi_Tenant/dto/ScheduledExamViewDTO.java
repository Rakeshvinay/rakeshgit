package com.brihathi.Multi_Tenant.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduledExamViewDTO {

    private Long scheduledExamId;
    private Long userId;

    private Long eduScheduledExamId;
    private Long educatorId;
    private Long tenantId;

    private String branchId;
    private String batchId;

    private String subjectId;
    private String chapterId;

    private String examType;
    private String difficulty;
    private String grade;

    private Integer totalMarks;
    private Long totalDuration;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
