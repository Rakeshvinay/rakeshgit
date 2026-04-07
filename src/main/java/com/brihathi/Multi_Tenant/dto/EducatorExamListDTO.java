package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class EducatorExamListDTO {

    private Long eduScheduledExamId;
    private String subject;
    private String branch;
    private String batch;
    private LocalDate scheduledDate;
    private LocalDateTime scheduledTime;
    private ExamStatus status;
    private Long studentCount;
    private Long durationMinutes;

}
