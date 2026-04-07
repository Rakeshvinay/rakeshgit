package com.brihathi.Multi_Tenant.entity;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;
 
import java.util.UUID;
 
@Entity
@Table(name = "difficulty_wise_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DifficultyWisePerformance {
 
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_difficulty_performance_user")
    )
    private User user;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", referencedColumnName = "exam_id", nullable = false)
    private Exam exam;
 
 
    @Column(name = "difficulty_level", nullable = false)
    private String difficultyLevel;
 
    @Column(name = "type", nullable = false)
    private String type; // Can be "UnAttempted", "WrongAnswers", or "CorrectAnswers"
 
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
 
 
 