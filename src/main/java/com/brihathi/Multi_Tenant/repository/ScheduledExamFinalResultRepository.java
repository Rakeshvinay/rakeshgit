package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledExamFinalResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduledExamFinalResultRepository
        extends JpaRepository<ScheduledExamFinalResult, Long> {

    Optional<ScheduledExamFinalResult> findByScheduledExamId(Long scheduledExamId);
    Optional<ScheduledExamFinalResult> findByScheduledExamIdAndUserId(Long scheduledExamId, Long userId);
    Optional<ScheduledExamFinalResult> findByEduScheduledExamIdAndUserId(Long eduScheduledExamId,Long userId);



    @Query("""
SELECT r.totalMarks, r.submittedDateTime
FROM ScheduledExamFinalResult r
WHERE r.userId = :userId
AND r.tenantId = :tenantId
ORDER BY r.submittedDateTime ASC
""")
List<Object[]> getScoreTrend(Long userId, Long tenantId);


}
