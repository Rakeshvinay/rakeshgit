package com.brihathi.Multi_Tenant.entity;

import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.converter.DurationToLongConverter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    @Column(name = "chapter_id", nullable = false)
    private String chapterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private Subject examType;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private String grade;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Convert(converter = DurationToLongConverter.class)
    @Column(name = "total_duration", nullable = false, updatable = false)
    private Duration totalDuration;

    // @Column(name = "start_time")
    // private LocalDateTime startTime;

    // @Column(name = "end_time")
    // private LocalDateTime endTime;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus status = ExamStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private Map<String, Object> summary;

    public enum ExamStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        ABORTED,
        UNATTEMPTED
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = ExamStatus.PENDING;
        }

        // if (this.totalMarks == null) {
        //     // if ("ALL".equals(this.examType)) {
        //         if (this.examType == Subject.ALL) {
        //         this.totalMarks = 720;
        //         this.totalDuration = Duration.ofMinutes(180); // stored as 10800 seconds
        //     } else {
        //         this.totalMarks = 180;
        //         this.totalDuration = Duration.ofMinutes(60); // stored as 3600 seconds
        //     }
        // }
        if (this.totalMarks == null) {

            // Case 1: All Subjects
            if (this.examType == Subject.ALL) {
                this.totalMarks = 720;
                this.totalDuration = Duration.ofMinutes(180);
            }
        
            // Case 2: Single Subject - All Chapters
            else if (this.chapterId != null && this.chapterId.endsWith("-ALL")) {
                this.totalMarks = 180;
                this.totalDuration = Duration.ofMinutes(60);
            }
        
            // Case 3: Single Subject - Single Chapter
            else {
                this.totalMarks = 120;
                this.totalDuration = Duration.ofMinutes(30);
            }
        }
    }
}
