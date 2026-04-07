
package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.QuestionTenant;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.dto.QuestionReviewDTO;
import com.brihathi.Multi_Tenant.dto.QuestionInsightDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionTenantRepository
        extends JpaRepository<QuestionTenant, String> {

    /* ===================== BASIC FINDERS ===================== */

    List<QuestionTenant> findBySubject(Subject subject);

    List<QuestionTenant> findBySubjectAndChapter(Subject subject, String chapter);

    List<QuestionTenant> findBySubjectAndDifficulty(
            Subject subject,
            Difficulty difficulty
    );

    List<QuestionTenant> findBySubjectAndGrade(
            Subject subject,
            String grade
    );

    Optional<QuestionTenant> findByQid(String qid);
    Optional<QuestionTenant> findByQidAndTenantId(String qid, Long tenantId);


    /* ===================== ALL SUBJECT SUPPORT ===================== */

    List<QuestionTenant> findByChapter(String chapter);

    List<QuestionTenant> findByDifficulty(Difficulty difficulty);

    List<QuestionTenant> findByGrade(String grade);

    /* ===================== DISTINCT FILTER VALUES ===================== */

    @Query("SELECT DISTINCT q.chapter FROM QuestionTenant q ORDER BY q.chapter")
    List<String> findAllDistinctChapters();

    @Query("SELECT DISTINCT q.chapter FROM QuestionTenant q WHERE q.subject = ?1 ORDER BY q.chapter")
    List<String> findDistinctChaptersBySubject(Subject subject);

    @Query("SELECT DISTINCT q.grade FROM QuestionTenant q ORDER BY q.grade")
    List<String> findAllDistinctGrades();

    @Query("SELECT DISTINCT q.grade FROM QuestionTenant q WHERE q.subject = ?1 ORDER BY q.grade")
    List<String> findDistinctGradesBySubject(Subject subject);

    /* ===================== COMBINED FILTERS ===================== */

    List<QuestionTenant> findBySubjectAndDifficultyAndGrade(
            Subject subject,
            Difficulty difficulty,
            String grade
    );

    List<QuestionTenant> findBySubjectAndDifficultyAndGradeAndChapterId(
            Subject subject,
            Difficulty difficulty,
            String grade,
            String chapterId
    );

    /* ===================== RANDOM QUESTION PICKING ===================== */

    @Query(
        value = """
        SELECT * FROM questions_tenant
        WHERE subject = CAST(:subject AS text)
          AND difficulty = CAST(:difficulty AS text)
          AND grade = :grade
        ORDER BY RANDOM()
        LIMIT :limit
        """,
        nativeQuery = true
    )
    List<QuestionTenant> findRandomQuestionsBySubject(
            @Param("subject") String subject,
            @Param("difficulty") String difficulty,
            @Param("grade") String grade,
            @Param("limit") int limit
    );

    @Query(
        value = """
        SELECT * FROM questions_tenant
        WHERE subject = CAST(:subject AS text)
          AND chapter_id = :chapterId
          AND difficulty = CAST(:difficulty AS text)
          AND grade = :grade
        ORDER BY RANDOM()
        LIMIT :limit
        """,
        nativeQuery = true
    )
    List<QuestionTenant> findRandomQuestionsBySubjectAndChapters(
            @Param("subject") String subject,
            @Param("chapterId") String chapterId,
            @Param("difficulty") String difficulty,
            @Param("grade") String grade,
            @Param("limit") int limit
    );

    /* ===================== SUBSCRIPTION BASED ===================== */

    @Query(
        value = """
        SELECT * FROM questions_tenant
        WHERE subject = CAST(:subject AS text)
          AND difficulty = CAST(:difficulty AS text)
          AND grade = :grade
          AND is_subscribed = :isSubscribed
        ORDER BY RANDOM()
        LIMIT :limit
        """,
        nativeQuery = true
    )
    List<QuestionTenant> findRandomQuestionsBySubjectAndSubscription(
            @Param("subject") String subject,
            @Param("difficulty") String difficulty,
            @Param("grade") String grade,
            @Param("isSubscribed") Boolean isSubscribed,
            @Param("limit") int limit
    );

    @Query(
        value = """
        SELECT * FROM questions_tenant
        WHERE subject = CAST(:subject AS text)
          AND chapter_id = :chapterId
          AND difficulty = CAST(:difficulty AS text)
          AND grade = :grade
          AND is_subscribed = :isSubscribed
        ORDER BY RANDOM()
        LIMIT :limit
        """,
        nativeQuery = true
    )
    List<QuestionTenant> findRandomQuestionsBySubjectAndChaptersAndSubscription(
            @Param("subject") String subject,
            @Param("chapterId") String chapterId,
            @Param("difficulty") String difficulty,
            @Param("grade") String grade,
            @Param("isSubscribed") Boolean isSubscribed,
            @Param("limit") int limit
    );

    /* ===================== FALLBACK ===================== */

    @Query(
        value = """
        SELECT * FROM questions_tenant
        WHERE subject = CAST(:subject AS text)
          AND is_active = true
        ORDER BY RANDOM()
        LIMIT :limit
        """,
        nativeQuery = true
    )
    List<QuestionTenant> findFallbackQuestions(
            @Param("subject") String subject,
            @Param("limit") int limit
    );

    /* ===================== VALIDATION & DEBUG ===================== */

    boolean existsByQuestionText(String questionText);

    @Query(
        value = """
        SELECT COUNT(*) FROM questions_tenant
        WHERE subject = CAST(:subject AS text)
          AND difficulty = CAST(:difficulty AS text)
          AND grade = :grade
        """,
        nativeQuery = true
    )
    long countBySubjectAndDifficultyAndGrade(
            @Param("subject") String subject,
            @Param("difficulty") String difficulty,
            @Param("grade") String grade
    );

    @Query(
        value = """
        SELECT COUNT(*) FROM questions_tenant
        WHERE subject = CAST(:subject AS text)
          AND difficulty = CAST(:difficulty AS text)
          AND grade = :grade
          AND is_subscribed = :isSubscribed
        """,
        nativeQuery = true
    )
    long countBySubjectAndDifficultyAndGradeAndSubscription(
            @Param("subject") String subject,
            @Param("difficulty") String difficulty,
            @Param("grade") String grade,
            @Param("isSubscribed") Boolean isSubscribed
    );



    
//     @Query("""
// SELECT new com.brihathi.Multi_Tenant.dto.QuestionReviewDTO(
//     r.questionId,
//     r.selectedOption,
//     q.questionText,
//     q.correctAnswerOption,
//     q.answerOption1,
//     q.answerOption2,
//     q.answerOption3,
//     q.answerOption4,
//     q.briefExplanation,
//     q.subject
// )
// FROM ScheduledExamResults r
// JOIN QuestionTenant q ON q.qid = r.questionId
// WHERE r.scheduledExamId = :scheduledExamId
// """)
// List<QuestionReviewDTO> getReviewFromTenant(Long scheduledExamId);



@Query("""
SELECT new com.brihathi.Multi_Tenant.dto.QuestionInsightDTO(
    q.qid,
    q.questionText,
    q.answerOption1,
    q.answerOption2,
    q.answerOption3,
    q.answerOption4,
    q.correctAnswerOption
)
FROM QuestionTenant q
WHERE q.qid IN :qids
""")
List<QuestionInsightDTO> fetchTenantQuestions(@Param("qids") List<String> qids);
// List<QuestionTenant> findByQidInre(List<QuestionTenant> questionsTenants);
List<QuestionTenant> findByQidIn(List<String> qids);

}
