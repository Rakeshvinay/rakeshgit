package com.brihathi.Multi_Tenant.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "chapter_wise_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterWiseResult {
 
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", referencedColumnName = "exam_id", nullable = false)
    private Exam exam;
   
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "subject", nullable = false)
    private String subject;
 
    @Column(name = "chapter", nullable = false)
    private String chapter;
 
    @Column(name = "difficulty_level")
    private String difficultyLevel;
 
    @Column(name = "marks")
    private Integer marks;
 
    @Column(name = "duration")
    private Integer duration;  // You can treat it as seconds or minutes
 
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
 
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "chapter_id")
    private String chapterId;
}
 
 
 