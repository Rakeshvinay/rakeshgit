package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
 

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByUser_UserId(Long userId);
    Optional<Exam> findByExamIdAndUser_UserId(Long examId, Long userId);
    List<Exam> findByUser_UserIdAndStatus(Long userId, Exam.ExamStatus status);
    List<Exam> findBySubjectIdAndChapterId(String subjectId, String chapterId);
    List<Exam> findByExamType(Subject examType);
    List<Exam> findByDifficulty(Difficulty difficulty);

    // @Query("SELECT COALESCE(SUM(e.totalDuration), 0) FROM Exam e WHERE e.user.userId = :userId AND e.examType = :examType")
    // Long getTotalTimeSpentByUserIdAndExamType(@Param("userId") Long userId, @Param("examType") String examType);
    @Query("SELECT COALESCE(SUM(e.totalDuration), 0) FROM Exam e WHERE e.user.userId = :userId AND e.examType = :examType")
    Long getTotalTimeSpentByUserIdAndExamType(@Param("userId") Long userId, @Param("examType") Subject examType);


    @Query("""
        SELECT e FROM Exam e
        WHERE e.status = :status
          AND e.updatedAt BETWEEN :start AND :end
    """)
    List<Exam> findRecentlyAbortedExams(
            @Param("status") Exam.ExamStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
     
} 
