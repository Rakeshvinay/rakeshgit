package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.entity.QuestionTenant;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.Data;

@Data
public class SimpleQuestionDTO {
    private String qid;
    private String questionText;
    private String answerOption1;
    private String answerOption2;
    private String answerOption3;
    private String answerOption4;  
    private Subject subject;

    public static SimpleQuestionDTO fromQuestion(QuestionPublic question) {
        SimpleQuestionDTO dto = new SimpleQuestionDTO();
        dto.setQid(question.getQid());
        dto.setQuestionText(question.getQuestionText());
        dto.setAnswerOption1(question.getAnswerOption1());
        dto.setAnswerOption2(question.getAnswerOption2());
        dto.setAnswerOption3(question.getAnswerOption3());
        dto.setAnswerOption4(question.getAnswerOption4());
        dto.setSubject(question.getSubject());
        return dto;
    }
} 