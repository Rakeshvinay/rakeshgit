package com.brihathi.Multi_Tenant.entity;

 
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;
 
@Entity
@Table(name = "your_score_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YourScoreProgress {
 
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_score_progress_user")
    )
    private User user;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
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
 
 