package com.brihathi.Multi_Tenant.repository;
 
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.brihathi.Multi_Tenant.entity.ScheduledExamFinalResult;
 
import java.util.List;
 
@Repository
public interface ScheduledOverallSwotRepository extends JpaRepository<ScheduledExamFinalResult, Long> {
 
//     // 1️⃣ Chapter-level aggregation (JPQL)
//     @Query("""
//         SELECT ch.subject, ch.chapterName,
//                SUM(ch.marks), SUM(ch.totalMarks), ch.timeSpent
//         FROM ScheduledChapterResult ch
//         WHERE  ch.userId = :userId
//         GROUP BY ch.subject, ch.chapterName, ch.timeSpent
//     """)
//     List<Object[]> aggregateChapterPerformanceForAllExams(@Param("userId") Long userId);
 
   
// @Query("""
//     SELECT ch.subject,
//            SUM(ch.marks),
//            SUM(ch.totalMarks),
//            ch.timeSpent,
//            COUNT(DISTINCT ch.chapterName)
//     FROM ScheduledChapterResult ch
//     WHERE  ch.userId = :userId
//     GROUP BY ch.subject, ch.timeSpent
// """)
// List<Object[]> aggregateSubjectInsights(@Param("userId") Long userId);
 
// @Query("""
//     SELECT ch.subject,
//            (SUM(ch.marks) * 180.0) /
//            (CASE WHEN SUM(ch.totalMarks) = 0 THEN 1 ELSE SUM(ch.totalMarks) END)
//     FROM ScheduledChapterResult ch
//     WHERE ch.userId = :userId
//     GROUP BY ch.subject
//     ORDER BY ch.subject
// """)
// List<Object[]> findAverageMarks(@Param("userId") Long userId);
 
 
// @Query("""
//     SELECT ch.subject,
//            ((SUM(ch.marks) * 180.0) /
//            (CASE WHEN SUM(ch.totalMarks) = 0 THEN 1 ELSE SUM(ch.totalMarks) END)) / 180 * 100
//     FROM ScheduledChapterResult ch
//     WHERE ch.userId = :userId
//     GROUP BY ch.subject
//     ORDER BY ch.subject
// """)
// List<Object[]> findAvgPercentage(@Param("userId") Long userId);
 
 
// @Query(value = """
//     SELECT
//       ch.subject,
//       ROUND(
//         (SUM(EXTRACT(EPOCH FROM ch.time_spent::interval)) / NULLIF(SUM(ch.total_marks) / 4.0, 0)) * 45 / 60,
//         2
//       ) AS avg_time_spent_per_subject_minutes
//     FROM scheduled_chapter_result ch
//     WHERE ch.user_id = :userId
//       AND ch.total_marks > 0
//       AND ch.time_spent IS NOT NULL
//     GROUP BY ch.subject
//     ORDER BY ch.subject
// """, nativeQuery = true)
// List<Object[]> findAverageTimeSpentPerSubject(@Param("userId") Long userId);
 
 
//  @Query("""
//     SELECT
//         e.subject,
//         ROUND(
//              (SUM(CASE WHEN e.marks = -1 THEN 1 ELSE 0 END) * 1.0 / COUNT(*)) * 45,
//         2
//         )
//     FROM ScheduledExamResult e
//     WHERE e.userId = :userId
//     GROUP BY e.subject
//     ORDER BY e.subject
// """)
// List<Object[]> getNegativeMarksSummary(@Param("userId") Long userId);

@Query(value = """
    SELECT subject, chapter_name,
           SUM(marks), SUM(total_marks),
           SUM(EXTRACT(EPOCH FROM time_spent::interval))
    FROM chapter_results
    WHERE user_id = :userId AND tenant_id = :tenantId
    GROUP BY subject, chapter_name
""", nativeQuery = true)
List<Object[]> aggregateChapterPerformance(@Param("userId") Long userId,
                                           @Param("tenantId") Long tenantId);

@Query(value = """
    SELECT subject,
           SUM(marks), SUM(total_marks),
           SUM(EXTRACT(EPOCH FROM time_spent::interval)),
           COUNT(DISTINCT chapter_name)
    FROM chapter_results
    WHERE user_id = :userId AND tenant_id = :tenantId
    GROUP BY subject
""", nativeQuery = true)
List<Object[]> aggregateSubjectInsights(@Param("userId") Long userId,
                                       @Param("tenantId") Long tenantId);

@Query(value = """
    SELECT SUM(EXTRACT(EPOCH FROM total_time_spent::interval))
    FROM scheduled_exam_final_results
    WHERE user_id = :userId AND tenant_id = :tenantId
""", nativeQuery = true)
Long getTotalTimeSpent(@Param("userId") Long userId,
                       @Param("tenantId") Long tenantId);
}
 