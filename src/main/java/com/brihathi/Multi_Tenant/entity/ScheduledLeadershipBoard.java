package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.*;
 
import java.util.UUID;
 
@Entity
@Table(name = "scheduled_exams_leadership_board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledLeadershipBoard {
 
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

    @Column(name = "examid")
    private Long examid;
 
 
    @Column(name = "total_marks")
    private Integer totalmarks;
}
 
 