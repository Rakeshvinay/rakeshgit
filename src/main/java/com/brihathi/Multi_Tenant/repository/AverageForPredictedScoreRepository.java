package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.ChapterResult;
 
import org.springframework.data.jpa.repository.Query;
 
import org.springframework.data.repository.Repository;
 
import org.springframework.data.repository.query.Param;
 
//import org.springframework.data.repository.Repository;
 
 
public interface AverageForPredictedScoreRepository extends Repository<ChapterResult, Long> {
 
    @Query(value = """
 
        SELECT COALESCE(AVG(weighted_score), 0) FROM (
 
            SELECT
 
                CASE
 
                    WHEN e.total_marks = 120 THEN SUM(cr.marks) * 6
 
                    WHEN e.total_marks = 180 THEN SUM(cr.marks) * 4
 
                    WHEN e.total_marks = 720 THEN SUM(cr.marks)
 
                    ELSE 0
 
                END AS weighted_score
 
            FROM chapter_results cr
 
            JOIN exams e ON cr.exam_id = e.exam_id
 
            WHERE cr.user_id = :userId
 
            -- Uncomment below line if you want to consider only completed exams
 
            -- AND e.status = 'COMPLETED'
 
            GROUP BY e.exam_id, e.total_marks
 
        ) t
 
        """, nativeQuery = true)
 
    Double findAvgScoreByUserId(@Param("userId") Long userId);
 
}

