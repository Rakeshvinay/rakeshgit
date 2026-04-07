package com.brihathi.Multi_Tenant.repository;
 
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.brihathi.Multi_Tenant.entity.ExamFinalResult;
 
import java.util.List;
 
@Repository
public interface OverallSwotRepository extends JpaRepository<ExamFinalResult, Long> {
 
    // 1️⃣ Chapter-level aggregation (JPQL)
    @Query("""
        SELECT ch.chapter.subject, ch.chapter.name,
               SUM(ch.marks), SUM(ch.totalMarks), ch.timeSpent
        FROM ChapterResult ch
        WHERE  ch.userId = :userId
        GROUP BY ch.chapter.subject, ch.chapter.name, ch.timeSpent
    """)
    List<Object[]> aggregateChapterPerformanceForAllExams(@Param("userId") Long userId);
 
   
@Query("""
    SELECT ch.chapter.subject,
           SUM(ch.marks),
           SUM(ch.totalMarks),
           ch.timeSpent,
           COUNT(DISTINCT ch.chapter.name)
    FROM ChapterResult ch
    WHERE  ch.userId = :userId
    GROUP BY ch.chapter.subject, ch.timeSpent
""")
List<Object[]> aggregateSubjectInsights(@Param("userId") Long userId);
 
@Query("""
    SELECT ch.chapter.subject,
           (SUM(ch.marks) * 180.0) /
           (CASE WHEN SUM(ch.totalMarks) = 0 THEN 1 ELSE SUM(ch.totalMarks) END)
    FROM ChapterResult ch
    WHERE ch.userId = :userId
    GROUP BY ch.chapter.subject
    ORDER BY ch.chapter.subject
""")
List<Object[]> findAverageMarks(@Param("userId") Long userId);
 
 
@Query("""
    SELECT ch.chapter.subject,
           ((SUM(ch.marks) * 180.0) /
           (CASE WHEN SUM(ch.totalMarks) = 0 THEN 1 ELSE SUM(ch.totalMarks) END)) / 180 * 100
    FROM ChapterResult ch
    WHERE ch.userId = :userId
    GROUP BY ch.chapter.subject
    ORDER BY ch.chapter.subject
""")
List<Object[]> findAvgPercentage(@Param("userId") Long userId);
 
 
@Query(value = """
    SELECT
      ch.subject,
      ROUND(
        (SUM(EXTRACT(EPOCH FROM ch.time_spent::interval)) / NULLIF(SUM(ch.total_marks) / 4.0, 0)) * 45 / 60,
        2
      ) AS avg_time_spent_per_subject_minutes
    FROM chapter_results ch
    WHERE ch.user_id = :userId
      AND ch.total_marks > 0
      AND ch.time_spent IS NOT NULL
    GROUP BY ch.subject
    ORDER BY ch.subject
""", nativeQuery = true)
List<Object[]> findAverageTimeSpentPerSubject(@Param("userId") Long userId);
 
 
 @Query("""
    SELECT
        e.subject,
        ROUND(
            (SUM(CASE WHEN e.marks = -1 THEN 1 ELSE 0 END) * 1.0 / COUNT(e)) * 45,
            2
        )
    FROM ExamResult e
    WHERE e.userId = :userId
    GROUP BY e.subject
    ORDER BY e.subject
""")
List<Object[]> getNegativeMarksSummary(@Param("userId") Long userId);
}
 