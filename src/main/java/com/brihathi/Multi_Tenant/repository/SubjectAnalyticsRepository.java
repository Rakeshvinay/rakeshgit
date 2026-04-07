package com.brihathi.Multi_Tenant.repository;



import com.brihathi.Multi_Tenant.entity.ScheduledExamFinalResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SubjectAnalyticsRepository extends JpaRepository<ScheduledExamFinalResult, Long> {

    @Query("""
        SELECT 
            CASE
                WHEN (r.totalMarks * 100.0) / 
                     (CASE WHEN s.examType = com.brihathi.Multi_Tenant.enums.Subject.ALL THEN 720 ELSE 180 END) >= 90 THEN 'ABOVE_90'
                WHEN (r.totalMarks * 100.0) / 
                     (CASE WHEN s.examType = com.brihathi.Multi_Tenant.enums.Subject.ALL THEN 720 ELSE 180 END) >= 80 THEN '80_90'
                WHEN (r.totalMarks * 100.0) / 
                     (CASE WHEN s.examType = com.brihathi.Multi_Tenant.enums.Subject.ALL THEN 720 ELSE 180 END) >= 70 THEN '70_80'
                WHEN (r.totalMarks * 100.0) / 
                     (CASE WHEN s.examType = com.brihathi.Multi_Tenant.enums.Subject.ALL THEN 720 ELSE 180 END) >= 60 THEN '60_70'
                WHEN (r.totalMarks * 100.0) / 
                     (CASE WHEN s.examType = com.brihathi.Multi_Tenant.enums.Subject.ALL THEN 720 ELSE 180 END) >= 50 THEN '50_60'
                ELSE 'BELOW_50'
            END,
            COUNT(DISTINCT r.userId)
        FROM ScheduledExamFinalResult r
        JOIN ScheduledExam s ON s.scheduledExamId = r.scheduledExamId
        WHERE r.tenantId = :tenantId
        AND (:branch IS NULL OR r.branchId = :branch)
        AND (:batch IS NULL OR r.batchId = :batch)
        GROUP BY 1
    """)
    List<Object[]> getPerformanceDistribution(Long tenantId, String branch, String batch);

    @Query("""
        SELECT COUNT(DISTINCT r.userId)
        FROM ScheduledExamFinalResult r
        WHERE r.tenantId = :tenantId
        AND (:branch IS NULL OR r.branchId = :branch)
        AND (:batch IS NULL OR r.batchId = :batch)
    """)
    Long getTotalStudents(Long tenantId, String branch, String batch);



    
@Query("""
    SELECT AVG(
        (r.totalMarks * 100.0) /
        (CASE WHEN s.examType = com.brihathi.Multi_Tenant.enums.Subject.ALL THEN 720 ELSE 180 END)
    )
    FROM ScheduledExamFinalResult r
    JOIN ScheduledExam s ON s.scheduledExamId = r.scheduledExamId
    WHERE r.tenantId = :tenantId
    AND (:branch IS NULL OR r.branchId = :branch)
    AND (:batch IS NULL OR r.batchId = :batch)
""")
Double getAveragePercentage(Long tenantId, String branch, String batch);



@Query("""
        SELECT r.subject,
               COUNT(DISTINCT r.userId),
               AVG(r.marks),
               AVG(r.percentage)
        FROM ScheduledChapterWiseResult r
        WHERE r.tenantId = :tenantId
        AND (:branch IS NULL OR r.branchId = :branch)
        AND (:batch IS NULL OR r.batchId = :batch)
        GROUP BY r.subject
    """)
    List<Object[]> getSubjectPerformance(
            @Param("tenantId") Long tenantId,
            @Param("branch") String branch,
            @Param("batch") String batch);

}
