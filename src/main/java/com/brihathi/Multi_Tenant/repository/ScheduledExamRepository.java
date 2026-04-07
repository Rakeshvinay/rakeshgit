package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledExam;
import com.brihathi.Multi_Tenant.dto.StudentResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.brihathi.Multi_Tenant.enums.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import java.time.LocalDateTime;
 



@Repository
public interface ScheduledExamRepository
        extends JpaRepository<ScheduledExam, Long> {

    // List<ScheduledExam> findByEduScheduledExamId(Long eduScheduledExamId);
    List<ScheduledExam> findByEduScheduledExamId(Long eduScheduledExamId);
    Optional<ScheduledExam> findByScheduledExamId(Long ScheduledExamId);

    boolean existsByEduScheduledExamIdAndUserId(
            Long eduScheduledExamId,
            Long userId
    );

    List<ScheduledExam> findByUserId(Long userId);
    List<ScheduledExam> findByEducatorId(Long educatorId);

    List<ScheduledExam> findByStatus(ScheduledExam.ExamStatus status);
    Optional<ScheduledExam> findByEduScheduledExamIdAndUserId(Long eduScheduledExamId,Long userId);
    Optional<ScheduledExam> findByScheduledExamIdAndUserId(Long scheduledExamId, Long userId);

    // Optional<ScheduledExam> findByEduScheduledExamIdAndTenantId(Long eduScheduledExamId, Long tenantId);

//     Optional<ScheduledExam> findByEduScheduledExamIdAndUserId(Long eduScheduledExamId,Long userId);
//     Optional<ScheduledExam> getTotalTimeSpentByUserIdAndExamType(Long userId, Subject subject);

    @Query("SELECT COALESCE(SUM(e.totalDuration), 0) FROM ScheduledExam e WHERE e.userId = :userId AND e.examType = :examType")
    Long getTotalTimeSpentByUserIdAndExamType(@Param("userId") Long userId, @Param("examType") Subject examType);


    @Modifying
@Query("""
UPDATE ScheduledExam s
SET s.status = com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus.CANCELLED
WHERE s.eduScheduledExamId = :eduScheduledExamId
""")
void cancelStudentExams(@Param("eduScheduledExamId") Long eduScheduledExamId);




@Query("""
SELECT new com.brihathi.Multi_Tenant.dto.StudentResultDTO(
    0,
    r.scheduledExamId,
    u.name,
    u.enrollmentId,
    u.branch,
    CASE 
        WHEN s.status = 'UNATTEMPTED' THEN 0
        ELSE COALESCE((r.correctedCount * 4) - r.wrongCount, 0)
    END,
    0.0,
     CASE
        WHEN s.status = 'UNATTEMPTED' THEN 'ABSENT'
        ELSE 'PRESENT'
    END
)
FROM ScheduledExam s
JOIN User u ON u.userId = s.userId
LEFT JOIN ScheduledExamResultsSummary r 
       ON r.scheduledExamId = s.scheduledExamId
WHERE s.eduScheduledExamId = :eduExamId
ORDER BY s.status ASC
""")
List<StudentResultDTO> getAllStudentResults(Long eduExamId);




@Query("""
SELECT 
SUM(CASE WHEN s.status = 'UNATTEMPTED' THEN 1 ELSE 0 END),
SUM(CASE WHEN s.status <> 'UNATTEMPTED' THEN 1 ELSE 0 END)
FROM ScheduledExam s
WHERE s.eduScheduledExamId = :eduExamId
""")
List<Object[]> getAttendanceStats(Long eduExamId);




@Query("""
    SELECT COUNT(se)
    FROM ScheduledExam se
    WHERE se.userId = :userId
    AND se.tenantId = :tenantId
    AND se.status IN ('COMPLETED','IN_PROGRESS','ABORTED')
    """)
    Long countAttempted(Long userId, Long tenantId);
    
    
    @Query("""
    SELECT COUNT(se)
    FROM ScheduledExam se
    WHERE se.userId = :userId
    AND se.tenantId = :tenantId
    AND se.status IN ('PENDING','UNATTEMPTED','CANCELLED')
    """)
    Long countUnattempted(Long userId, Long tenantId);
    
    @Query("""
        SELECT s.scheduledExamId
        FROM ScheduledExam s
        JOIN User u ON u.userId = s.userId
        WHERE s.eduScheduledExamId = :eduExamId
        AND u.enrollmentId = :rollNo
    """)
    Long findScheduledExamIdByEduExamAndUser(Long eduExamId, String rollNo);
    


    @Query("""
        SELECT s FROM ScheduledExam s
        WHERE s.status = :status
        AND s.updatedAt BETWEEN :from AND :to
    """)
    List<ScheduledExam> findRecentlyAbortedExams(
            @Param("status") ScheduledExam.ExamStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );



    long countByEduScheduledExamId(Long eduScheduledExamId);

long countByEduScheduledExamIdAndStatusIn(
        Long eduScheduledExamId,
        List<ScheduledExam.ExamStatus> statuses
);
}
