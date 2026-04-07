package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.dto.EducatorExamListDTO;
import com.brihathi.Multi_Tenant.entity.EducatorScheduledExam;
import com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
 


@Repository
public interface EducatorScheduledExamRepository
        extends JpaRepository<EducatorScheduledExam, Long> {
            List<EducatorScheduledExam> findByExamStatus(ExamStatus examStatus);

            Optional<EducatorScheduledExam> findByEduScheduledExamId(Long eduScheduledExamId);

            @Query("""
                SELECT e FROM EducatorScheduledExam e
                  WHERE e.tenantId = (SELECT t.tenantId FROM Tenant t WHERE t.subdomain = :subdomain)
                  AND (:branch IS NULL OR e.branch = :branch)
                  AND (:batch IS NULL OR e.batch = :batch)
                  AND (:examStatus IS NULL OR e.examStatus = :examStatus)
            """)
            List<EducatorScheduledExam> findScheduledExamsByFilters(
                String subdomain,
                String branch,
                String batch,
                String examStatus
            );



            Optional<EducatorScheduledExam> findById(Long eduScheduledExamId);



            @Query("""
    SELECT e FROM EducatorScheduledExam e
    WHERE e.tenantId = :tenantId
      AND e.branch = :branch
      AND e.batch = :batch
      AND e.examStatus IN ('PENDING', 'IN_PROGRESS')
""")
List<EducatorScheduledExam> findActiveExamsForBatch(
        Long tenantId,
        String branch,
        String batch
);
Optional<EducatorScheduledExam> findByEduScheduledExamIdAndTenantId(
  Long eduScheduledExamId, Long tenantId);



  @Query("""
SELECT new com.brihathi.Multi_Tenant.dto.EducatorExamListDTO(
    e.eduScheduledExamId,
    CAST(e.examType AS string),
    e.branch,
    e.batch,
    e.scheduledDate,
    e.scheduledTime,
    e.examStatus,
    COUNT(s.scheduledExamId),
    COALESCE(MAX(s.totalDuration) / 60, 0)
)
FROM EducatorScheduledExam e
LEFT JOIN ScheduledExam s
    ON s.eduScheduledExamId = e.eduScheduledExamId
WHERE e.tenantId = :tenantId
AND (:branch IS NULL OR e.branch = :branch)
AND (:batch IS NULL OR e.batch = :batch)
AND (:examStatus IS NULL OR e.examStatus = :examStatus)
GROUP BY
    e.eduScheduledExamId,
    e.examType,
    e.branch,
    e.batch,
    e.scheduledDate,
    e.scheduledTime,
    e.examStatus
ORDER BY e.scheduledTime DESC
""")

List<EducatorExamListDTO> findEducatorExamDashboard(
        Long tenantId,
        String branch,
        String batch,
        String examStatus
);

@Query("""
SELECT e.examStatus, COUNT(e)
FROM EducatorScheduledExam e
WHERE e.tenantId = :tenantId
GROUP BY e.examStatus
""")
List<Object[]> getExamStatusCounts(Long tenantId);


 
@Query("""
    SELECT e FROM EducatorScheduledExam e
    WHERE e.scheduledDate = :date
    AND e.scheduledTime BETWEEN :now AND :nextTime
""")
List<EducatorScheduledExam> findUpcomingExams(
        @Param("date") LocalDate date,
        @Param("now") LocalDateTime now,
        @Param("nextTime") LocalDateTime nextTime
);
 
 @Query("""
    SELECT e FROM EducatorScheduledExam e
    WHERE e.scheduledDate = :date
    AND e.examEndTime BETWEEN :now AND :nextTime
""")
List<EducatorScheduledExam> findEndingExams(
        @Param("date") LocalDate date,
        @Param("now") LocalDateTime now,
        @Param("nextTime") LocalDateTime nextTime
);
 
 
// @Query("""
//     SELECT e FROM EducatorScheduledExam e
//     WHERE e.examStatus = 'CANCELLED'
// """)
// List<EducatorScheduledExam> findCancelledExams();
 
 
@Query("""
    SELECT e FROM EducatorScheduledExam e
    WHERE e.examStatus = com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus.CANCELLED
    AND e.updatedAt BETWEEN :from AND :to
 
""")
List<EducatorScheduledExam> findCancelledExams(@Param("from") LocalDateTime from,
@Param("to") LocalDateTime to);



@Query("""
    SELECT e FROM EducatorScheduledExam e
    WHERE e.examStatus = 'COMPLETED'
      AND e.updatedAt BETWEEN :from AND :to
""")
List<EducatorScheduledExam> findCompletedExams(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
);
// @Query("""
//     SELECT DISTINCT e FROM EducatorScheduledExam e
//     JOIN ScheduledExam s ON s.eduScheduledExamId = e.eduScheduledExamId
//     WHERE s.status = 'COMPLETED'
// """)
// List<EducatorScheduledExam> findCompletedExams();
}
