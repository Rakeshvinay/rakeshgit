package com.brihathi.Multi_Tenant.entity;
import com.brihathi.Multi_Tenant.enums.ReportValidated;
 
import jakarta.persistence.*;
import lombok.*;
 
import java.util.UUID;
 
@Entity
@Table(name = "examquestionsreport")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuestionsReport {
 
    @Id
    @Column(nullable = false)
    private UUID uuid;
 
    @Column(name = "user_id", nullable = false)
    private Long userId;
 
    @Column(name = "q_id", nullable = false)
    private String qId;
    
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;
 
    @Column(name = "answered_option")
    private String answeredOption;
 
    @Column(name = "correct_answer_option")
    private String correctAnswerOption;
 
    @Column(name = "report", columnDefinition = "TEXT")
    private String report;
 
    @Column(name = "answer_option1")
    private String answerOption1;
 
    @Column(name = "answer_option2")
    private String answerOption2;
 
    @Column(name = "answer_option3")
    private String answerOption3;
 
    @Column(name = "answer_option4")
    private String answerOption4;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "is_validated")
    private ReportValidated isValidated;
 
    @Column(name = "actions_taken", columnDefinition = "TEXT")
    private String actionsTaken;
 
}
