package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EducatorScheduledExamResponse {

    private Long eduScheduledExamId;

    private Long educatorId;
    private Long tenantId;

    private Subject examType;     // ALL
    private String subjectId;     // ALL
    private Difficulty difficulty;
    private String grade;

    private String branch;
    private String batch;

    private LocalDate scheduledDate;
    private LocalDateTime scheduledTime;
    private LocalDateTime examEndTime;

    private ExamStatus examStatus;    // PENDING

    private LocalDateTime createdAt;
}
