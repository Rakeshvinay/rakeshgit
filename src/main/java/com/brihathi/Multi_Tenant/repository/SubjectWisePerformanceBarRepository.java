
package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.SubjectWisePerformanceBar;
import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
import com.brihathi.Multi_Tenant.dto.ChapterWisePerformanceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.UUID;
 
 
@Repository
public interface SubjectWisePerformanceBarRepository extends JpaRepository<SubjectWisePerformanceBar, UUID> {
    List<SubjectWisePerformanceBar> findByUser_UserId(Long userId);
    List<SubjectWisePerformanceBar> findByUser_UserIdAndSubject(Long userId, String subject);
 
  @Query(value = """
        SELECT
            subject AS subject,
            SUM(percentage) AS totalPercentage,
            COUNT(*) AS countRows,
            ROUND(SUM(percentage) / COUNT(*)) AS averagePercentage
        FROM  
            subject_wise_performance_bar
        WHERE
            user_id = :userId
            AND created_at BETWEEN :startDate AND :endDate
        GROUP BY
            subject
        ORDER BY
            averagePercentage DESC
        """, nativeQuery = true)
    List<SubjectWisePerformanceDTO> getSubjectWisePerformanceByUser(
            @Param("userId") Long userId,
            @Param("startDate")java.time. LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );
 
    @Query(value = """
        SELECT
            chapter AS chapter,
            ROUND(AVG(percentage)) AS averagePercentage
        FROM
            chapter_wise_results
        WHERE
            user_id = :userId
           
            AND LOWER(subject) = LOWER(:subject)
            AND created_at >= :startDate
            AND created_at <= :endDate
        GROUP BY
            chapter
        ORDER BY averagePercentage DESC
        """, nativeQuery = true)
    List<ChapterWisePerformanceDTO> findChapterWisePerformanceByUserAndDifficultyAndSubject(
        @Param("userId") Long userId,
       
        @Param("subject") String subject,
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate
    );
}
 
