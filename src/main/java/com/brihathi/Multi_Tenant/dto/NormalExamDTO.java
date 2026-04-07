package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class NormalExamDTO {

    private Long examId;
    private String examType;
    private String subjectId;
    private Difficulty difficulty;
    private String chapterId;
    private String grade;

    private Integer totalMarks;
    private String totalDuration;

    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<QuestionPreviewDTO> questions;
}
