package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.*;
 
import java.util.UUID;
 
@Entity
@Table(name = "subject_wise_performance_bar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectWisePerformanceBar {
 
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_performance_bar_user")
    )
    private User user;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
 
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
 
 
 