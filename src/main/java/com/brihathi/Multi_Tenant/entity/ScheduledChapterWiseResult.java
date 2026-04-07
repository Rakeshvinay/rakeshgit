package com.brihathi.Multi_Tenant.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "scheduled_chapter_wise_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledChapterWiseResult {
 
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
 
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
 
    @Column(name = "chapter", nullable = false)
   private String chapter;

    @Column(name = "difficulty_level")
    private String difficultyLevel;
 
    @Column(name = "marks")
    private Integer marks;
 
    @Column(name = "duration")
    private Integer duration;  // You can treat it as seconds or minutes
 
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
 
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "chapter_id")
    private String chapterId;
}
 
