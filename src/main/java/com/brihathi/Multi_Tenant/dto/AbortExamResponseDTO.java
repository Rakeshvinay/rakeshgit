package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.entity.Exam.ExamStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AbortExamResponseDTO {

    private Long userId;
    private Long examId;
    private Long tenantId;

    private String subjectId;     // ZOO
    private String chapterId;     // ZOO-ALL
    private Subject examType;     // ZOOLOGY

    private Difficulty difficulty;
    private String grade;

    private Integer totalMarks;
    private Long totalDuration;   // seconds

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private ExamStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
