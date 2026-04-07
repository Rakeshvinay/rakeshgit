package com.brihathi.Multi_Tenant.dto;
 
import lombok.*;
import com.brihathi.Multi_Tenant.enums.Subject;
@NoArgsConstructor
@AllArgsConstructor              
 
 
@Data @Builder
public class ErrorTrackerResponseDTO {
 
    private Long   id;
    private Long   examId;
    private String qid;
 
    private String questionText;
    private Subject subject;
 
    private String answerOption1;
    private String answerOption2;
    private String answerOption3;
    private String answerOption4;
 
    private String answeredOption;
    private String correctAnswerOption;
    private String briefExplanation;
}
 
 