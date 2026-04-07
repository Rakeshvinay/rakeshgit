package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheuledDifficultyWisePerformance;
import com.brihathi.Multi_Tenant.dto.DifficultyWisePerformanceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
public interface ScheduledDifficultyWisePerformanceRepository extends JpaRepository<ScheuledDifficultyWisePerformance, Long> {

 @Query(value = """
    SELECT
        difficulty_level AS difficultyLevel,
        ROUND(AVG(CASE WHEN type = 'CorrectAnswers' THEN percentage END), 2) AS avgCorrectPercentage,
        ROUND(AVG(CASE WHEN type = 'WrongAnswers' THEN percentage END), 2) AS avgWrongPercentage,
        ROUND(AVG(CASE WHEN type = 'UnAttempted' THEN percentage END), 2) AS avgUnattemptedPercentage
    FROM
        scheduled_difficulty_wise_performance
    WHERE
        user_id = :userId
        AND created_at BETWEEN :startDate AND :endDate
    GROUP BY
        difficulty_level
    ORDER BY
        CASE
            WHEN difficulty_level = 'BASIC' THEN 1
            WHEN difficulty_level = 'INTERMEDIATE' THEN 2
            WHEN difficulty_level = 'ADVANCED' THEN 3
            ELSE 4
        END
    """, nativeQuery = true)
List<DifficultyWisePerformanceDTO> getDifficultyWisePerformance(
    @Param("userId") Long userId,
    @Param("startDate") java.time.LocalDateTime startDate,
    @Param("endDate") java.time.LocalDateTime endDate
);




Optional<ScheuledDifficultyWisePerformance>
findByUserIdAndScheduledExamIdAndDifficultyLevelAndType(
        Long userId,
        Long scheduledExamId,
        String difficultyLevel,
        String type
);

}
