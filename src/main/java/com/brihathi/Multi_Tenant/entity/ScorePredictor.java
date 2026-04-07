package com.brihathi.Multi_Tenant.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
 
import java.time.LocalDateTime;
import java.util.UUID;
 
@Entity
@Table(name = "score_predictor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScorePredictor {
 
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_user")
    )
    private User user;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
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
 
   
 
