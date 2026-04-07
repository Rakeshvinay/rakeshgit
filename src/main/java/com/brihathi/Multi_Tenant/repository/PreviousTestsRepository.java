
package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.dto.PreviousTestDTO;
import com.brihathi.Multi_Tenant.entity.ChapterResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
 
@Repository
public interface PreviousTestsRepository extends JpaRepository<ChapterResult, Long> {
 
   
    @Query(value = """
    SELECT
        cr.exam_id AS exam_id,
        cr.exam_type AS examType,
        SUM(cr.marks) AS marks,
        CASE
            WHEN (SUM(cr.marks) * 100.0 / NULLIF(SUM(cr.total_marks), 0)) < 0 THEN 0
            ELSE (SUM(cr.marks) * 100.0 / NULLIF(SUM(cr.total_marks), 0))
        END AS percentage,
        MAX(e.end_date) AS endDate,
	e.difficulty AS difficulty
    FROM chapter_results cr
    JOIN exams e ON cr.exam_id = e.exam_id
    WHERE cr.user_id = :userId
      AND e.status = 'COMPLETED'
    GROUP BY cr.exam_id, cr.exam_type, e.difficulty
    ORDER BY MAX(e.end_date) DESC
    """, nativeQuery = true)
List<PreviousTestDTO> findPreviousTestsByUserId(@Param("userId") long userId);
 
   
 
}
 
 
