package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;
 
@Entity
@Table(name = "scheduled_exams_your_score_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledYourScoreProgress {
 
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID uuid;
 
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(
    //     name = "user_id",
    //     nullable = false,
    //     foreignKey = @ForeignKey(name = "fk_score_progress_user")
    // )
    // private User user;

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
 
    @Column(name = "subject", nullable = false)
    private String subject;
 
    @Column(name = "avg_marks")
    private Integer avgMarks;
 
    @Column(name = "date")
    private LocalDate date;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
 
 