package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ChapterWiseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ChapterWiseResultRepository extends JpaRepository<ChapterWiseResult, UUID> {
    // Add custom queries if needed
    java.util.List<ChapterWiseResult> findByUser_UserIdAndSubject(Long userId, String subject);
    java.util.List<ChapterWiseResult> findByUser_UserIdAndExam_ExamId(Long userId, Long examId);
} 