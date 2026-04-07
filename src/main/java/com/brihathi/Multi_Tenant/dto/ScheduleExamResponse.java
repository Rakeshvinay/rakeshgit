package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class ScheduleExamResponse {

    private EducatorScheduledExamResponse educatorScheduledExam;
    private ExamPreviewResponse exam;
}
