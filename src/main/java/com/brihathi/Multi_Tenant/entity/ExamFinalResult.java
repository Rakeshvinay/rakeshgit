package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat; 
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
@Entity
@Table(name = "exam_final_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamFinalResult {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "exam_id", nullable = false)
    private Long examId;
 
    @Column(name = "total_time_spent", nullable = false)
    private String totalTimeSpent;

    @JsonFormat(pattern = "dd-MM-yyyy, hh:mm:ss a", timezone = "Asia/Kolkata")

    @Column(name = "submitted_date_time", nullable = false)
    private ZonedDateTime submittedDateTime;
 
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;
 
    @OneToMany(mappedBy = "examFinalResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapterResult> chapters;
 
    @Column(name = "tenant_id")
    private Long tenantId;
    
}
 
