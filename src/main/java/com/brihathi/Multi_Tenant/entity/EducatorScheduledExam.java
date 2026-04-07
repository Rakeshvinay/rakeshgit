package com.brihathi.Multi_Tenant.entity;

import com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus;
import jakarta.persistence.*;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "educators_scheduled_exams")
public class EducatorScheduledExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edu_scheduled_exam_id")
    private Long eduScheduledExamId;

    @Column(name = "educator_id")
    private Long educatorId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "branch", length = 250)
    private String branch;

    @Column(name = "batch", length = 250)
    private String batch;


    // @Column(name = "exam_type", length = 100)
    // private String examType;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private Subject examType;

    // @Column(name = "exam_status", length = 100)
    // private String examStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "exam_status")
    private ExamStatus examStatus;

    @Column(name = "grade", length = 50)
    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 50)
    private Difficulty difficulty;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
  
    @UpdateTimestamp    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Kolkata")
@Column(name = "exam_end_time", nullable = false)
private LocalDateTime examEndTime;

}
