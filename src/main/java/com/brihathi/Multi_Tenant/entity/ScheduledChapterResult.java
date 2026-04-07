package com.brihathi.Multi_Tenant.entity;

// import javax.security.auth.Subject;
import com.brihathi.Multi_Tenant.enums.Subject;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "scheduled_chapter_results")
public class ScheduledChapterResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "scheduled_exam_id", nullable = false)
    private Long scheduledExamId;

    @Column(name = "edu_scheduled_exam_id")
    private Long eduScheduledExamId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "educator_id")
    private Long educatorId;

    @Column(name = "branch_id", length = 250)
    private String branchId;

    @Column(name = "batch_id", length = 250)
    private String batchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type")
    private Subject examType;

    @Column(name = "chapter_id", nullable = false)
    private String chapterId;

    @Column(name = "chapter_name", nullable = false)
    private String chapterName;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "time_spent", nullable = false, length = 8)
    private String timeSpent;

    @Column(name = "percentage", nullable = false)
    private Double percentage;

    @Column(name = "marks", nullable = false)
    private Integer marks;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "ai_analysis", nullable = false, columnDefinition = "TEXT")
    private String aiAnalysis;
}
