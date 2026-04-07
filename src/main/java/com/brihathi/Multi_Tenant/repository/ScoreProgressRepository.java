package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.YourScoreProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.brihathi.Multi_Tenant.dto.ScoreProgressResponseDTO;
 
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
 
 
import java.util.List;
import java.util.UUID;
@Repository
public interface ScoreProgressRepository extends JpaRepository<YourScoreProgress, Long> {
 
    @Query(value = """
        SELECT
            subject AS subject,
            SUM(avg_marks) AS totalPercentage,
            COUNT(*) AS countRows,
            ROUND(SUM(avg_marks) * 1.0 / COUNT(*), 2) AS averagePercentage
        FROM  
            your_score_progress
        WHERE
            user_id = :userId
            AND created_at BETWEEN :startDate AND :endDate
        GROUP BY
            subject
        ORDER BY
            averagePercentage DESC
        """, nativeQuery = true)
    List<ScoreProgressResponseDTO> getScoreProgress(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
 
 