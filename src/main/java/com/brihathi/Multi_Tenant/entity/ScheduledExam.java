package com.brihathi.Multi_Tenant.entity;

import jakarta.persistence.*;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.QuestionStatus;
import com.brihathi.Multi_Tenant.enums.Subject;

import lombok.Data;
import java.util.List;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "scheduled_exams")
public class ScheduledExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduled_exam_id")
    private Long scheduledExamId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "edu_scheduled_exam_id", nullable = false)
    private Long eduScheduledExamId;

    @Column(name = "educator_id", nullable = false)
    private Long educatorId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "branch_id", nullable = false)
    private String branchId;

    @Column(name = "batch_id", nullable = false)
    private String batchId;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    @Column(name = "chapter_id", nullable = false)
    private String chapterId;

    @Column(name = "qid")
    private List<String> qidsList;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private Subject examType;
    

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name = "grade", nullable = false)
    private String grade;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "total_duration", nullable = false)
    private Long totalDuration;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    // @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private ExamStatus status = ExamStatus.PENDING;

    public enum ExamStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        ABORTED,
        UNATTEMPTED,
        CANCELLED;

        int toUpperCase() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'toUpperCase'");
        } 
    }

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
