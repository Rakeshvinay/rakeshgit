package com.brihathi.Multi_Tenant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_exam_results_summary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledExamResultsSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "total_answered", nullable = false)
    private Integer totalAnswered;

    @Column(name = "not_answered", nullable = false)
    private Integer notAnswered;

    @Column(name = "marked_for_review", nullable = false)
    private Integer markedForReview;

    @Column(name = "answered_and_marked_for_review", nullable = false)
    private Integer answeredAndMarkedForReview;

    @Column(name = "not_visited", nullable = false)
    private Integer notVisited;

    @Column(name = "difficulty_level")
    private String difficultyLevel;
 
    @Column(name = "corrected_count")
    private Integer correctedCount;
 
    @Column(name = "wrong_count")
    private Integer wrongCount;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


} 