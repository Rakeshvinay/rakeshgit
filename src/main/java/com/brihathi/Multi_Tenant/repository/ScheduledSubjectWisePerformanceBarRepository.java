 
// package com.brihathi.Multi_Tenant.repository;
 
// import com.brihathi.Multi_Tenant.entity.ScheduledSubjectWisePerformanceBar;
// import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
// import com.brihathi.Multi_Tenant.dto.ChapterWisePerformanceDTO;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
 
// import java.util.List;
// import java.util.UUID;
 
 
// @Repository
// public interface ScheduledSubjectWisePerformanceBarRepository extends JpaRepository<ScheduledSubjectWisePerformanceBar, UUID> {
//     List<ScheduledSubjectWisePerformanceBar> findByUserId(Long userId);
//     //List<ScheduledSubjectWisePerformanceBarMains> findByUser_UserIdAndSubject(Long userId, String subject);
 

 
//     @Query(value = """
//         SELECT
//             subject AS subject,
//             SUM(percentage) AS totalPercentage,
//             COUNT(*) AS countRows,
//             ROUND(SUM(percentage) / COUNT(*)) AS averagePercentage
//         FROM  
//             scheduled_exams_subject_wise_performance_bar
//         WHERE
//             user_id = :userId
//             AND created_at BETWEEN :startDate AND :endDate
//         GROUP BY
//             subject
//         ORDER BY
//             averagePercentage DESC
//         """, nativeQuery = true)
//     List<SubjectWisePerformanceDTO> getSubjectWisePerformanceByUser(
//             @Param("userId") Long userId,
//             @Param("startDate")java.time. LocalDateTime startDate,
//             @Param("endDate") java.time.LocalDateTime endDate
//     );
 
//     @Query(value = """
//         SELECT
//             chapter AS chapter,
//             ROUND(AVG(percentage)) AS averagePercentage
//         FROM
//             scheduled_chapter_wise_results    
//         WHERE
//             user_id = :userId
           
//             AND LOWER(subject) = LOWER(:subject)
//             AND created_at >= :startDate
//             AND created_at <= :endDate
//         GROUP BY
//             chapter
//         ORDER BY averagePercentage DESC
//         """, nativeQuery = true)
//     List<ChapterWisePerformanceDTO> findChapterWisePerformanceByUserAndDifficultyAndSubject(
//         @Param("userId") Long userId,
       
//         @Param("subject") String subject,
//         @Param("startDate") java.time.LocalDateTime startDate,
//         @Param("endDate") java.time.LocalDateTime endDate
//     );
// }

package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledSubjectWisePerformanceBar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface ScheduledSubjectWisePerformanceBarRepository
        extends JpaRepository<ScheduledSubjectWisePerformanceBar, UUID> {

    Optional<ScheduledSubjectWisePerformanceBar>
    findByScheduledExamIdAndUserIdAndSubject(
            Long scheduledExamId,
            Long userId,
            String subject
    );

    List<ScheduledSubjectWisePerformanceBar> findByUserId(Long userId);



    @Query(value = """
SELECT p.subject,
       SUM(p.percentage) AS totalPercentage,
       COUNT(*) AS countRows,
       AVG(p.percentage) AS averagePercentage
FROM scheduled_exams_subject_wise_performance_bar p
WHERE p.branch_id = :branchName
  AND p.tenant_id = :tenantId
GROUP BY p.subject
""", nativeQuery = true)
List<Object[]> getBranchSubjectPerformance(
        @Param("branchName") String branchName,
        @Param("tenantId") Long tenantId
);



@Query("""
SELECT
    SUM(s.percentage),
    COUNT(s),
    AVG(s.percentage),
    s.subject
FROM ScheduledSubjectWisePerformanceBar s
WHERE s.userId = :userId
AND s.tenantId = :tenantId
GROUP BY s.subject
""")
List<Object[]> getStudentSubjectPerformance(Long userId, Long tenantId);

}
