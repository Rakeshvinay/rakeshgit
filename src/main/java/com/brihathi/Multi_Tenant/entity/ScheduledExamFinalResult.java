package com.brihathi.Multi_Tenant.entity;
 
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "scheduled_exam_final_results")
public class ScheduledExamFinalResult {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
 
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
 
    @Column(name = "total_time_spent", nullable = false)
    private String totalTimeSpent;
 
    @JsonFormat(pattern = "dd-MM-yyyy, hh:mm:ss a", timezone = "Asia/Kolkata")
 
    @Column(name = "submitted_date_time", nullable = false)
    private ZonedDateTime submittedDateTime;
 
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;
   
}
 
 