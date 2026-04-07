package com.brihathi.Multi_Tenant.entity;
// import com.brihathi.multitenant.enums.QuestionType;
import java.time.Duration;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.converter.DurationToLongConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "scheduled_examresults")
public class ScheduledExamResult {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
 
    @Column(name = "scheduled_exam_id")
    private Long scheduledExamId;
 
    @Column(name = "edu_scheduled_exam_id")
    private Long eduScheduledExamId;
 
    @Column(name = "user_id")
    private Long userId;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "educator_id")
    private Long educatorId;
 
    @Column(name = "branch_id")
    private String branchId;
 
    @Column(name = "batch_id")
    private String batchId;
 
    @Column(name = "qid")
    private String qid;
 
    @Column(name = "subject")
    private String subject;
 
    @Column(name = "chapter")
    private String chapter;
 
    @Column(name = "answered")
    private Boolean answered = false;
 
    @Column(name = "visited")
    private Boolean visited=false;
 
    @Column(name = "marked_for_review")
    private Boolean markedForReview=false;
 
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
   
    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type")
    private Subject examType;
 
    // public String getQid() { return qid; }
    // public void setQId(String qId) { this.qid = qId; }
    
 
}
 
 