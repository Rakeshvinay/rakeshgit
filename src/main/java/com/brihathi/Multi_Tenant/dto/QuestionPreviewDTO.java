package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class QuestionPreviewDTO {

    private String qid;
    private String subject;
    private String chapter;
    private String chapterId;
    // private String questionType;
    private String questionText;

    private String answerOption1;
    private String answerOption2;
    private String answerOption3;
    private String answerOption4;
}
