package com.brihathi.Multi_Tenant.dto;
 
import lombok.Data;
 
@Data
public class ExamFinalResultDTO {
    private Long examId;
    private Long userId;
    private Integer totalQuestions;
    private Integer totalAnswered;
    private Integer notAnswered;
    private Integer markedForReview;
    private Integer notVisited;
    private Integer answeredAndMarkedForReview;
    private Integer totalMarks;
    private Integer correctCount;
    private Integer wrongCount;
    private Long totalDurationSeconds;
}