package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.ScheduledChapterResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledAverageForPredictedScoreRepository extends JpaRepository<ScheduledChapterResult, Long> {
 
    @Query(value = """
        SELECT COALESCE(AVG(weighted_score), 0) FROM (
            SELECT
                CASE
                    WHEN e.total_marks = 100 THEN SUM(cr.marks) * 3
                    WHEN e.total_marks = 300 THEN SUM(cr.marks) 
                    ELSE 0
                END AS weighted_score
            FROM scheduled_chapter_results cr
            JOIN scheduled_exams e ON cr.edu_scheduled_exm_id = e.edu_scheduled_exm_id
            WHERE cr.user_id = :userId
            -- Uncomment below line if you want to consider only completed exams
            AND e.status = 'COMPLETED'
            GROUP BY e.edu_scheduled_exm_id, e.total_marks
        ) t
        """, nativeQuery = true)
    Double findAvgScoreByUserId(@Param("userId") Long userId);
 
}
