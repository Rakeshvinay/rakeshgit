package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.QuestionStatus;
import com.brihathi.Multi_Tenant.enums.Subject;
// import com.brihathi.Multi_Tenant.enums.SubscriptionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionDTO {
    private String qid;
    private Subject subject;
    private String chapter;
    private String chapterId;
    private String questionText;
    private String answerOption1;
    private String answerOption2;
    private String answerOption3;
    private String answerOption4;
    private String correctAnswerOption;
    private String topic;
    private String subTopic;
    private String grade;
    private String briefExplanation;
    private Boolean isPyq;
    private Integer timesRepeated;
    private String source;
    private Difficulty difficulty;
    // private LocalDateTime startDate;
    // private LocalDateTime endDate;
    private QuestionStatus status;
    // private Boolean isSubscribed;

    public static QuestionDTO fromEntity(QuestionPublic question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQid(question.getQid());
        dto.setSubject(question.getSubject());
        dto.setChapter(question.getChapter());
        dto.setChapterId(question.getChapterId());
        dto.setQuestionText(question.getQuestionText());
        dto.setAnswerOption1(question.getAnswerOption1());
        dto.setAnswerOption2(question.getAnswerOption2());
        dto.setAnswerOption3(question.getAnswerOption3());
        dto.setAnswerOption4(question.getAnswerOption4());
        dto.setCorrectAnswerOption(question.getCorrectAnswerOption());
        dto.setTopic(question.getTopic());
        dto.setSubTopic(question.getSubTopic());
        dto.setGrade(question.getGrade());
        dto.setBriefExplanation(question.getBriefExplanation());
        dto.setIsPyq(question.getIsPyq());
        dto.setTimesRepeated(question.getTimesRepeated());
        dto.setSource(question.getSource());
        dto.setDifficulty(question.getDifficulty());
        // dto.setStartDate(question.getStartDate());
        // dto.setEndDate(question.getEndDate());
        dto.setStatus(question.getStatus());
        // dto.setIsSubscribed(question.getIsSubscribed());
        return dto;
    }

    public QuestionPublic toEntity() {
        QuestionPublic question = new QuestionPublic();
        question.setQid(this.qid);
        question.setSubject(this.subject);
        question.setChapter(this.chapter);
        question.setChapterId(this.chapterId);
        question.setQuestionText(this.questionText);
        question.setAnswerOption1(this.answerOption1);
        question.setAnswerOption2(this.answerOption2);
        question.setAnswerOption3(this.answerOption3);
        question.setAnswerOption4(this.answerOption4);
        question.setCorrectAnswerOption(this.correctAnswerOption);
        question.setTopic(this.topic);
        question.setSubTopic(this.subTopic);
        question.setGrade(this.grade);
        question.setBriefExplanation(this.briefExplanation);
        question.setIsPyq(this.isPyq);
        question.setTimesRepeated(this.timesRepeated);
        question.setSource(this.source);
        question.setDifficulty(this.difficulty);
        // question.setStartDate(this.startDate);
        // question.setEndDate(this.endDate);
        question.setStatus(this.status);
        // question.setIsSubscribed(this.isSubscribed);
        return question;
    }
} 