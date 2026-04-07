package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.*;
 
import java.util.UUID;
 
@Entity
@Table(name = "scheduled_exams_subject_wise_performance_bar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledSubjectWisePerformanceBar {
 
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID uuid;
 
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
 
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
 
    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "difficulty_level")
    private String difficultyLevel;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
 
 