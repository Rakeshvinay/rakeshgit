package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "exam_results_summary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultsSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
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
 