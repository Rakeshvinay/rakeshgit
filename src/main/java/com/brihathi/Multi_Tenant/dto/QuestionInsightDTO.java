package com.brihathi.Multi_Tenant.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionInsightDTO {

    private String questionId;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;

    private Long wrongCount;
    private Long skippedCount;
    private Double avgTimeSpent;

    // ✅ REQUIRED FOR JPQL "SELECT new"
    public QuestionInsightDTO(
            String questionId,
            String questionText,
            String option1,
            String option2,
            String option3,
            String option4,
            String correctAnswer
    ) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
    }
}
