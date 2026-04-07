// package com.brihathi.Multi_Tenant.repository;

// import com.brihathi.Multi_Tenant.entity.ScheduledExamResultsSummary;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.Optional;
// import java.util.*;

// @Repository
// public interface ScheduledExamResultsSummaryRepository extends JpaRepository<ScheduledExamResultsSummary, Long> {
//     Optional<ScheduledExamResultsSummary> findByUserIdAndEduScheduledExamId(Long userId, Long examId);
//     List<ScheduledExamResultsSummary> findByEduScheduledExamIdAndUserId( Long examId,Long userId);
 
// } 


package com.brihathi.Multi_Tenant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brihathi.Multi_Tenant.entity.ScheduledExamResultsSummary;
import com.brihathi.Multi_Tenant.dto.StudentResultDTO;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ScheduledExamResultsSummaryRepository
        extends JpaRepository<ScheduledExamResultsSummary, Long> {

            Optional<ScheduledExamResultsSummary> findByScheduledExamId(Long scheduledExamId);

    Optional<ScheduledExamResultsSummary>
    findByScheduledExamIdAndUserId(Long scheduledExamId, Long userId);
    List<ScheduledExamResultsSummary>findAllByScheduledExamIdAndUserId(Long scheduledExamId, Long userId);
    
    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.StudentResultDTO(
            0,
            r.scheduledExamId,
            u.name,
            u.enrollmentId,
            u.branch,
            (r.correctedCount * 4) - r.wrongCount,
            ((r.correctedCount * 4) - r.wrongCount) * 100.0 / r.totalQuestions
        )
        FROM ScheduledExamResultsSummary r
        JOIN User u ON u.userId = r.userId
        WHERE r.eduScheduledExamId = :eduExamId
        ORDER BY ((r.correctedCount * 4) - r.wrongCount) DESC
        """)
        List<StudentResultDTO> getStudentResults(Long eduExamId);
        
    

        @Query("""
            SELECT 
            AVG((r.correctedCount * 4) - r.wrongCount),
            MAX((r.correctedCount * 4) - r.wrongCount),
            COUNT(r),
            SUM(CASE WHEN ((r.correctedCount * 4) - r.wrongCount) >= 40 THEN 1 ELSE 0 END)
            FROM ScheduledExamResultsSummary r
            WHERE r.eduScheduledExamId = :eduExamId
            """)
            List<Object[]> getExamSummary(Long eduExamId);
            

}
