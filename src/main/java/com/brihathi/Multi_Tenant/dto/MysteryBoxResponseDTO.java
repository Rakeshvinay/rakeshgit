package com.brihathi.Multi_Tenant.dto;
 
import lombok.*;
import com.brihathi.Multi_Tenant.enums.Subject;
             
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MysteryBoxResponseDTO {
 
    private Long id;
    private Long examId;
    private Long tenantId;
    private String qid;
 
    private String questionText;
    private Subject subject;
 
    private String answerOption1;
    private String answerOption2;
    private String answerOption3;
    private String answerOption4;
 
    private String correctAnswerOption;
    private String briefExplanation;
 
    // ⭐ JPQL constructor (WITHOUT tenantId)
    public MysteryBoxResponseDTO(
            Long id,
            Long examId,
            String qid,
            String questionText,
            Subject subject,
            String answerOption1,
            String answerOption2,
            String answerOption3,
            String answerOption4,
            String correctAnswerOption,
            String briefExplanation
    ) {
        this.id = id;
        this.examId = examId;
        this.qid = qid;
        this.questionText = questionText;
        this.subject = subject;
        this.answerOption1 = answerOption1;
        this.answerOption2 = answerOption2;
        this.answerOption3 = answerOption3;
        this.answerOption4 = answerOption4;
        this.correctAnswerOption = correctAnswerOption;
        this.briefExplanation = briefExplanation;
    }
}
 
 