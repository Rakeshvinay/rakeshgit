package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledChapterWiseResult;
import com.brihathi.Multi_Tenant.dto.ChapterPerformanceAnalyticsDTO;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduledChapterAnalyticsRepository extends JpaRepository<ScheduledChapterWiseResult, Long> {

    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.ChapterPerformanceAnalyticsDTO(
            r.chapterId,
            r.chapter,
            r.subject,
            AVG(r.percentage)
        )
        FROM ScheduledChapterWiseResult r
        WHERE r.tenantId = :tenantId
          AND (:branch IS NULL OR r.branchId = :branch)
          AND (:batch IS NULL OR r.batchId = :batch)
          AND (:subject IS NULL OR r.subject = :subject)
        GROUP BY r.chapterId, r.chapter, r.subject
        ORDER BY AVG(r.percentage) DESC
    """)
    List<ChapterPerformanceAnalyticsDTO> getChapterPerformance(
            @Param("tenantId") Long tenantId,
            @Param("branch") String branch,
            @Param("batch") String batch,
            @Param("subject") String subject
    );
}
