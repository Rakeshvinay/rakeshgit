package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Duration;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllScheduleExamsResDTO {

    private Long scheduledExamId;
    private Long userId;

    private String batchId;
    private String branchId;

    private Long eduScheduledExamId;
    private Long tenantId;

    private LocalDate scheduledDate;
    private LocalDateTime scheduledTime;
    private LocalDateTime examEndTime;

    private ScheduledExam.ExamStatus status;

    private String grade;
    private Difficulty difficulty;

    private Long totalMarks;
    private Duration totalDuration;

    private Subject examType;
}
