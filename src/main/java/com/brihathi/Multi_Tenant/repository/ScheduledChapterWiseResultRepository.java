// package com.brihathi.Multi_Tenant.repository;

// import com.brihathi.Multi_Tenant.entity.ScheduledChapterWiseResult;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import java.util.UUID;

// @Repository
// public interface ScheduledChapterWiseResultRepository extends JpaRepository<ScheduledChapterWiseResult, UUID> {
//     // Add custom queries if needed
//     java.util.List<ScheduledChapterWiseResult> findByUserIdAndSubject(Long userId, String subject);
//     java.util.List<ScheduledChapterWiseResult> findByUserIdAndEduScheduledExamId(Long userId, Long eduScheduledExamId);
// } 


package com.brihathi.Multi_Tenant.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brihathi.Multi_Tenant.entity.ScheduledChapterWiseResult;

@Repository
public interface ScheduledChapterWiseResultRepository
        extends JpaRepository<ScheduledChapterWiseResult, UUID> {

    Optional<ScheduledChapterWiseResult>
    findByScheduledExamIdAndUserIdAndSubjectAndChapter(
            Long ScheduledExamId,
            Long userId,
            String subject,
            String chapter
    );


     // 🔽 ADD THIS
     List<ScheduledChapterWiseResult> findByUserId(Long userId);

     List<ScheduledChapterWiseResult>
findByScheduledExamIdAndUserId(Long scheduledExamId, Long userId);

}
