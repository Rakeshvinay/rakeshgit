package com.brihathi.Multi_Tenant.entity;
 
import java.time.LocalTime;
 
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDate;
 
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.CreationTimestamp;
 
// import com.brihathi.Multi_Tenant.enums.Category;
import com.brihathi.Multi_Tenant.enums.NotificationStatus;
// import com.brihathi.Multi_Tenant.enums.PaperType;
import com.brihathi.Multi_Tenant.enums.Subject;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
 
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Multi Tenant
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
 
    // User
    @Column(name = "user_id", nullable = true)
    private Long userId;
 
 
    @Column(name = "educator_id", nullable = true)
    private Long educatorId;
 
    @Column(name = "exam_id", nullable = true)
    private Long examId;
 
    // @Column(name = "paper_type", nullable = true)
    // private PaperType paperType;
 
    // Exam Mapping
    @Column(name = "edu_scheduled_exam_id", nullable = true)
    private Long eduScheduledExamId;
 
    @Column(name = "scheduled_exam_id", nullable = true)
    private Long scheduledExamId;
 
    // Batch + Branch
    @Column(name = "batch_id")
    private String batchId;
 
    @Column(name = "branch_id")
    private String branchId;
 
    // Message
    @Column(length = 255)
    private String title;
 
    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private Subject examType;
 
    // @Enumerated(EnumType.STRING)
    // @Column(name = "category", length = 255)
    // private Category category;
 
    @Column(columnDefinition = "TEXT")
    private String message;
 
    // Scheduler
    @Column(name = "schedule_time")
    private LocalTime scheduleTime;
 
    @Column(name="scheduled_date")
    private LocalDate scheduledDate;
 
    @Column(name = "end_time")
    private LocalTime endTime;
 
    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status= NotificationStatus.PENDING;
 
    @Column(name = "total_duration", length = 20)
    private String totalDuration;
 
 
    // Retry
    @Column(name = "retry_count")
    private Integer retryCount = 0;
 
    // Audit
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    // // Audit
    // @CreationTimestamp
    
}
 
 