package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
import com.brihathi.Multi_Tenant.dto.ExamFinalResultDTO;
import com.brihathi.Multi_Tenant.dto.ChapterResultDTO;
 
@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByExamId(Long examId);
    List<ExamResult> findByExamIdAndQid(Long examId, String qid);
    List<ExamResult> findByExamIdAndUserId(Long examId, Long userId);
   
    @Query("SELECT er.qid FROM ExamResult er WHERE er.examId IN (SELECT e.examId FROM Exam e WHERE e.user.userId = :userId ORDER BY e.createdAt DESC LIMIT 1)")
    List<String> findRecentQidsForUser(@Param("userId") Long userId);
 
    @Query(value = """
        SELECT
            er.exam_id as examId,
            er.user_id as userId,
            COUNT(*) as totalQuestions,
            SUM(CASE WHEN er.answered = true THEN 1 ELSE 0 END) as totalAnswered,
            SUM(CASE WHEN er.answered = false THEN 1 ELSE 0 END) as notAnswered,
            SUM(CASE WHEN er.marked_for_review = true THEN 1 ELSE 0 END) as markedForReview,
            SUM(CASE WHEN er.visited = false THEN 1 ELSE 0 END) as notVisited,
            SUM(CASE WHEN er.answered = true AND er.marked_for_review = true THEN 1 ELSE 0 END) as answeredAndMarkedForReview,
            SUM(CASE WHEN er.validate_answer = 'correct' THEN 4 WHEN er.validate_answer = 'wrong' THEN -1 ELSE 0 END) as totalMarks,
            SUM(CASE WHEN er.validate_answer = 'correct' THEN 1 ELSE 0 END) as correctCount,
            SUM(CASE WHEN er.validate_answer = 'wrong' THEN 1 ELSE 0 END) as wrongCount,
            SUM(CAST(er.duration AS BIGINT)) as totalDurationSeconds
        FROM examresults er
        WHERE er.exam_id = :examId AND er.user_id = :userId
        GROUP BY er.exam_id, er.user_id
        """, nativeQuery = true)
    ExamFinalResultDTO generateExamFinalResult(@Param("examId") Long examId, @Param("userId") Long userId);
 
    @Query(value = """
        SELECT
            er.chapter as chapterName,
            er.subject as subject,
            COUNT(*) as totalQuestions,
            SUM(CASE WHEN er.answered = true THEN 1 ELSE 0 END) as answeredQuestions,
            SUM(CASE WHEN er.validate_answer = 'correct' THEN 4 WHEN er.validate_answer = 'wrong' THEN -1 ELSE 0 END) as chapterMarks,
            SUM(CASE WHEN er.validate_answer = 'correct' THEN 1 ELSE 0 END) as correctAnswers,
            SUM(CASE WHEN er.validate_answer = 'wrong' THEN 1 ELSE 0 END) as wrongAnswers,
            SUM(CASE WHEN er.answered = false THEN 1 ELSE 0 END) as unansweredQuestions,
            SUM(CAST(er.duration AS BIGINT)) as totalDurationSeconds,
            ROUND(
                (SUM(CASE WHEN er.validate_answer = 'correct' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2
            ) as percentage
        FROM examresults er
        WHERE er.exam_id = :examId AND er.user_id = :userId
        GROUP BY er.chapter, er.subject
        ORDER BY er.chapter
        """, nativeQuery = true)
    List<ChapterResultDTO> generateChapterResults(@Param("examId") Long examId, @Param("userId") Long userId);
 
 
    /* WRONG answers in this exam. */
    @Query("""
        SELECT r
        FROM   ExamResult r
        WHERE  r.userId            = :userId
          AND  r.examId            = :examId
          AND  LOWER(r.validateAnswer)  in ('wrong','incorrect','w','WRONG')
    """)
    List<ExamResult> findWrong(@Param("userId") Long userId,
                               @Param("examId") Long examId);
 
    /* RIGHT answers in this exam. */
    @Query("""
        SELECT r
        FROM   ExamResult r
        WHERE  r.userId            = :userId
          AND  r.examId            = :examId
          AND  LOWER(r.validateAnswer)  in ('correct','CORRECT','c','CORRECT')
    """)
    List<ExamResult> findRight(@Param("userId") Long userId,
                               @Param("examId") Long examId);



 List<ExamResult> findByExamIdAndUserIdAndTenantId( Long examId, Long userId,    Long tenantId     );

 
 @Query("""
    SELECT e.qid
    FROM ExamResult e
    WHERE e.examId = :examId
      AND e.userId = :userId
      AND e.tenantId = :tenantId""")
List<String> findQidsByExamIdAndUserIdAndTenantId( Long examId,  Long userId,    Long tenantId);


Optional<ExamResult> findByExamIdAndUserIdAndTenantIdAndQid(Long examId,Long userId,Long tenantId,String qid);



@Query("""
    SELECT r.answerOption
    FROM ExamResult r
    WHERE r.examId = :examId
      AND r.userId = :userId
      AND r.qid = :qid
""")
String findAnswerOption(
        @Param("examId") Long examId,
        @Param("userId") Long userId,
        @Param("qid") String qid
);

@Query("""
    SELECT er.qid
    FROM ExamResult er
    WHERE er.userId = :userId
      AND er.examId = :examId
      AND er.validateAnswer IS NOT NULL
""")
List<String> findAttemptedQids(@Param("userId") Long userId, @Param("examId") Long examId);
 

}
 
