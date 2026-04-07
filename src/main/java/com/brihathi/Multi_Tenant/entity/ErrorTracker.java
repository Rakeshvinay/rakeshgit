package com.brihathi.Multi_Tenant.entity;
 
import com.brihathi.Multi_Tenant.enums.Subject;
 
import jakarta.persistence.*;
import lombok.*;
 
@Entity
@Table(name = "error_tracker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorTracker {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "exam_id", nullable = false)
    private Long examId;
 
     @Column(name = "user_id", nullable = false)
    private Long userId; 

    @Column(name = "qid", nullable = false)
    private String qid;

    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;
 
    @Column(name = "answered_option")
    private String answeredOption;
 
    @Column(name = "correct_answer_option")
    private String correctAnswerOption;
   
    @Enumerated(EnumType.STRING)
    @Column(name = "subject", length = 100)
    private Subject subject;
 
    @Column(name = "answer_option1")
    private String answerOption1;
 
    @Column(name = "answer_option2")
    private String answerOption2;
 
    @Column(name = "answer_option3")
    private String answerOption3;
 
    @Column(name = "answer_option4")
    private String answerOption4;
 
    @Column(name = "brief_explanation", columnDefinition = "TEXT")
    private String briefExplanation;
}
 
