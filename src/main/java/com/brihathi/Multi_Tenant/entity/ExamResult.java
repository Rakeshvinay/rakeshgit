package com.brihathi.Multi_Tenant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Duration;

import com.brihathi.Multi_Tenant.converter.DurationToLongConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "exam_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "qid", nullable = false)
    private String qid;

    @Column(name = "answered")
    private Boolean answered = false;

    @Column(name = "visited")
    private Boolean visited = false;

    @Column(name = "marked_for_review")
    private Boolean markedForReview = false;

    @Convert(converter = DurationToLongConverter.class)
    @Column(name = "duration")
    private Duration duration = Duration.ZERO;

    @Column(name = "answer_option")
    private String answerOption;

    @JsonIgnore
    @Column(name = "correct_answer_option")
    private String correctAnswerOption;

    @JsonIgnore
    @Column(name = "validate_answer")
    private String validateAnswer;

    @Column(name = "marks")
    private Integer marks;

    @Column(name = "chapter")
    private String chapter;

    @Column(name = "subject")
    private String subject;
}
