package com.brihathi.Multi_Tenant.dto;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionReviewDTO {

    private String qId;
    private String answerOption;

    private String questionText;
    private String correctAnswerOption;
    private String answerOption1;
    private String answerOption2;
    private String answerOption3;
    private String answerOption4;
    private String briefExplanation;
    private Subject subject;



    public QuestionReviewDTO(
        String qId,
        String answerOption,
        String questionText,
        String correctAnswerOption,
        String answerOption1,
        String answerOption2,
        String answerOption3,
        String answerOption4,
        String briefExplanation,
        Subject subject
) {
    this.qId = qId;
    this.answerOption = answerOption;
    this.questionText = questionText;
    this.correctAnswerOption = correctAnswerOption;
    this.answerOption1 = answerOption1;
    this.answerOption2 = answerOption2;
    this.answerOption3 = answerOption3;
    this.answerOption4 = answerOption4;
    this.briefExplanation = briefExplanation;
    this.subject = subject;
}

}
