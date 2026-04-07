package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledChapterResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledChapterResultRepository
        extends JpaRepository<ScheduledChapterResult, Long> {

    List<ScheduledChapterResult> findByScheduledExamId(Long scheduledExamId);
    List<ScheduledChapterResult> findByScheduledExamIdAndUserId(Long scheduledExamId, Long userId);
    

}
