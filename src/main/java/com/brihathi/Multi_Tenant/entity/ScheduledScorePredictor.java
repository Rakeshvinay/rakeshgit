package com.brihathi.Multi_Tenant.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
 
import java.time.LocalDateTime;
import java.util.UUID;
 
@Entity
@Table(name = "scheduled_exams_score_predictor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledScorePredictor {
 
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID uuid;
 
    // @Column(name = "scheduled_exam_id")
    // private Long scheduledExamId;
 
    // @Column(name = "edu_scheduled_exam_id")
    // private Long eduScheduledExamId;
 
    @Column(name = "user_id")
    private Long userId;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    // @Column(name = "educator_id")
    // private Long educatorId;
 
    @Column(name = "branch_id")
    private String branchId;
 
    @Column(name = "batch_id")
    private String batchId;
 
    @Column(name = "predicted_score")
    private Double predictedScore;
 
    @Column(name = "no_of_exams")
    private Integer noOfExams;
 
    @Column(name = "predicted_rank")
    private Integer predictedRank;

    @Column(name = "percentage")
    private Double percentage;
 
    @Column(name = "good_at", columnDefinition = "TEXT")
    private String goodAt;
 
    @Column(name = "need_to_improve", columnDefinition = "TEXT")
    private String needToImprove;
 
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
 
   
 
 
 