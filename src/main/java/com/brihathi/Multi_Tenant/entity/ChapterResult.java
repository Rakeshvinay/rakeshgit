package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.brihathi.Multi_Tenant.enums.Subject;
 
@Entity
@Table(name = "chapter_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResult {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @Column(name = "exam_id")
    private Long examId;
 
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tenant_id")
    private Long tenantId;
    
    @ManyToOne
    @JoinColumn(name = "exam_final_result_id", nullable = false)
    private ExamFinalResult examFinalResult;
 
    @Embedded
    private Chapter chapter;
   
    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type")
    private Subject examType;
 
    @Column(name = "time_spent", nullable = false)
    private String timeSpent;
 
    @Column(name = "percentage", nullable = false)
    private Double percentage;
 
    @Column(name = "marks", nullable = false)
    private Integer marks;
 
    @Column(name = "ai_analysis", nullable = false)
    private String aiAnalysis;
 
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;
 
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chapter {
        @Column(name = "chapter_id", nullable = false)
        private String id;
 
        @Column(name = "chapter_name", nullable = false)
        private String name;
 
        @Column(name = "subject", nullable = false)
        private String subject;
    }
}
 