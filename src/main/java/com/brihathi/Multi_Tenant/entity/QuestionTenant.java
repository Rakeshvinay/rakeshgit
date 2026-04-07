package com.brihathi.Multi_Tenant.entity;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.QuestionStatus;
import com.brihathi.Multi_Tenant.enums.Subject;
// import com.brihathi.Multi_Tenant.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions_tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTenant {
    @Id
    @Column(name = "qid", length = 8)
    private String qid;

    @Column(name = "tenant_id")
    private Long tenantId;



    @Enumerated(EnumType.STRING)
    @Column(name = "subject", nullable = false)     
    private Subject subject;

    @Column(name = "chapter", nullable = false, length = 100)
    private String chapter;

    @Column(name = "chapter_id", length = 50)
    private String chapterId;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "brief_explanation", columnDefinition = "TEXT")
    private String briefExplanation;

    @Column(name = "answer_option1", nullable = false, columnDefinition = "TEXT")
    private String answerOption1;

    @Column(name = "answer_option2", nullable = false, columnDefinition = "TEXT")
    private String answerOption2;

    @Column(name = "answer_option3", nullable = false, columnDefinition = "TEXT")
    private String answerOption3;

    @Column(name = "answer_option4", nullable = false, columnDefinition = "TEXT")
    private String answerOption4;

    @Column(name = "correct_answer_option", nullable = false, length = 10)
    private String correctAnswerOption;

    @Column(name = "topic", nullable = false, length = 50)
    private String topic;

    @Column(name = "sub_topic", length = 100)
    private String subTopic;

    @Column(name = "grade", nullable = false, length = 20)
    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuestionStatus status;

    @Column(name = "is_pyq")
    private Boolean isPyq;

    @Column(name = "times_repeated")
    private Integer timesRepeated;

    @Column(name = "source", length = 255)
    private String source;

    // @Column(name = "is_subscribed", nullable = false)
    // private Boolean isSubscribed;

    @PrePersist
    public void prePersist() {
        if (this.timesRepeated == null) {
            this.timesRepeated = 0;
        }
    }
} 