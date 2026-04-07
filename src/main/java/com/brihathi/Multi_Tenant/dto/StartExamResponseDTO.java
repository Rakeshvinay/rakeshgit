package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.entity.Exam;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StartExamResponseDTO {

    private Long examId;
    private String subjectId;
    private String chapterId;
    private String examType;
    private Difficulty difficulty;
    private String grade;
    private Integer totalMarks;
    private String totalDuration;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Exam.ExamStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
