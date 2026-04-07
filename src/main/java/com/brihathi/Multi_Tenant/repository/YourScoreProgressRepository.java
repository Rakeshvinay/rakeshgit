package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.YourScoreProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
 
@Repository
public interface YourScoreProgressRepository extends JpaRepository<YourScoreProgress, UUID> {
    // Add custom queries if needed
    @Query(value = """
        SELECT
            subject,
            FLOOR(EXTRACT(DAY FROM (created_at - :startDate)) / 7) + 1 AS week,
            ROUND(AVG(avg_marks), 2) AS averagePercentage,
            SUM(avg_marks) AS totalPercentage,
            COUNT(*) AS countRows
        FROM your_score_progress
        WHERE user_id = :userId
          AND created_at BETWEEN :startDate AND :endDate
        GROUP BY subject, week
        ORDER BY subject, week
        """, nativeQuery = true)
    List<Object[]> getScoreProgressByWeek(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
 
    @Query(value = """
        SELECT
            subject,
            ROUND(AVG(avg_marks), 2) AS averagePercentage,
            SUM(avg_marks) AS totalPercentage,
            COUNT(*) AS countRows
        FROM your_score_progress
        WHERE user_id = :userId
          AND created_at BETWEEN :startDate AND :endDate
        GROUP BY subject
        ORDER BY subject
        """, nativeQuery = true)
    List<Object[]> getScoreProgressSubjectSummary(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
 
    @Query(value = "SELECT MIN(created_at) FROM your_score_progress WHERE user_id = :userId", nativeQuery = true)
    java.sql.Timestamp findFirstExamDate(@Param("userId") Long userId);
}
 