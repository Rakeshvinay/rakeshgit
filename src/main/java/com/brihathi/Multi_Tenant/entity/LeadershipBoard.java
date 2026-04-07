package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.*;
 
import java.util.UUID;
 
@Entity
@Table(name = "leadership_board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadershipBoard {
 
    @Id
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_leadership_user")
    )
    private User user;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "examid")
    private Long examid;
 
 
    @Column(name = "total_marks")
    private Integer totalmarks;
}
 
 
 
