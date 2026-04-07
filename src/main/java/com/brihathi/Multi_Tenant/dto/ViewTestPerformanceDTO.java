package com.brihathi.Multi_Tenant.dto;

 
public interface ViewTestPerformanceDTO {
    String getQuestionId();              // qid
    String getQuestionText();
    String getAnswerOption1();
    String getAnswerOption2();
    String getAnswerOption3();
    String getAnswerOption4();
    String getBriefExplanation();
    String getAnswerOption();           // user-selected
    String getCorrectAnswerOption();
    String getSubject();
}
 