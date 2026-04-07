// package com.brihathi.Multi_Tenant.repository;
 
// import com.brihathi.Multi_Tenant.entity.ScheduledTimeAnalysis;
// import com.brihathi.Multi_Tenant.dto.TimeAnalysisDTO;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import java.time.LocalDateTime;
// import java.util.List;
 
 
// import java.util.UUID;
 
 
// @Repository
// public interface ScheduledTimeAnalysisRepository extends JpaRepository<ScheduledTimeAnalysis, Long> {
 
//     @Query(value = """
//       SELECT
//     sd.subject,
//     sd.total_time_spent,
//     sd.noof_exams,
//     sd.average_percentage,
//     ROUND(sd.average_percentage / t.total_avg_percentage * 200, 5) AS raw_percentage
// FROM (
//     SELECT
//         subject,
//         SUM(avg_time) AS total_time_spent,
//         COUNT(*) AS noof_exams,
//         ROUND(SUM(avg_time) * 1.0 / COUNT(*), 6) AS average_percentage
//     FROM
//         scheduled_time_analysis
//     WHERE
//         user_id = :userId
//          AND created_at BETWEEN :startDate AND :endDate
//     GROUP BY
//         subject
// ) sd
// JOIN (
//     SELECT
//         SUM(avg_percentage) AS total_avg_percentage
//     FROM (
//         SELECT
//             ROUND(SUM(avg_time) * 1.0 / COUNT(*), 6) AS avg_percentage
//         FROM
//             scheduled_time_analysis
//         WHERE
//             user_id = :userId
//              AND created_at BETWEEN :startDate AND :endDate
//         GROUP BY
//             subject
//     ) x
// ) t ON true
// ORDER BY
//     sd.average_percentage DESC;
//         """, nativeQuery = true)
//     List<Object[]> getSubjectTimeAnalysisWithRawPercentage(
//         @Param("userId") Long userId,
//         @Param("startDate") java.time.LocalDateTime startDate,
//         @Param("endDate") java.time.LocalDateTime endDate
//     );
// }
package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledTimeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledTimeAnalysisRepository
        extends JpaRepository<ScheduledTimeAnalysis, UUID> {

    Optional<ScheduledTimeAnalysis>
    findByScheduledExamIdAndUserIdAndSubject(
            Long scheduledExamId,
            Long userId,
            String subject
    );
}
