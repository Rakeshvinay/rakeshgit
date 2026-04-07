package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;
 
@Entity
@Table(name = "time_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeAnalysis {
 
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_time_analysis_user")
    )
    private User user;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "subject", nullable = false, length = 100)
    private String subject;
 
    @Column(name = "avg_time")
    private Integer avgTime;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
 
 
 