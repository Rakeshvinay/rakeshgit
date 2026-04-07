package com.brihathi.Multi_Tenant.dto;
 
public interface RetryTestDTO {
    String getQuestionId();              // qid
    String getQuestionText();
    String getAnswerOption1();
    String getAnswerOption2();
    String getAnswerOption3();
    String getAnswerOption4(); 
    String getCorrectAnswerOption();          
    String getSubject();
    String getChapterId();    // changed from Long to String
    String getDifficulty();
    String getExamType();
    String getGrade();
}
 