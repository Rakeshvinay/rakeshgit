package com.brihathi.Multi_Tenant.dto;
 
import lombok.Data;
 
@Data
public class ChapterResultDTO {
    private String chapterName;
    private String subject;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer chapterMarks;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unansweredQuestions;
    private Long totalDurationSeconds;
    private Double percentage;
}
 