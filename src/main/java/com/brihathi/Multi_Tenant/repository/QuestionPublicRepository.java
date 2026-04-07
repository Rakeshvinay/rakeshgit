package com.brihathi.Multi_Tenant.repository;
import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.dto.QuestionInsightDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionPublicRepository extends JpaRepository<QuestionPublic, Integer> {
    List<QuestionPublic> findBySubject(Subject subject);
    List<QuestionPublic> findBySubjectAndChapter(Subject subject, String chapter);
    List<QuestionPublic> findBySubjectAndDifficulty(Subject subject, Difficulty difficulty);
    List<QuestionPublic> findBySubjectAndGrade(Subject subject, String grade);
    Optional<QuestionPublic> findByQid(String qid);

    // New methods for ALL subject queries
    List<QuestionPublic> findByChapter(String chapter);
    List<QuestionPublic> findByDifficulty(Difficulty difficulty);
    List<QuestionPublic> findByGrade(String grade);

    @Query("SELECT DISTINCT q.chapter FROM QuestionPublic q ORDER BY q.chapter")
    List<String> findAllDistinctChapters();

    @Query("SELECT DISTINCT q.chapter FROM QuestionPublic q WHERE q.subject = ?1 ORDER BY q.chapter")
    List<String> findDistinctChaptersBySubject(Subject subject);

    @Query("SELECT DISTINCT q.grade FROM QuestionPublic q ORDER BY q.grade")
    List<String> findAllDistinctGrades();

    @Query("SELECT DISTINCT q.grade FROM QuestionPublic q WHERE q.subject = ?1 ORDER BY q.grade")
    List<String> findDistinctGradesBySubject(Subject subject);

    List<QuestionPublic> findBySubjectAndDifficultyAndGrade(Subject subject, Difficulty difficulty, String grade);
    List<QuestionPublic> findBySubjectAndDifficultyAndGradeAndChapterId(Subject subject, Difficulty difficulty, String grade, String chapterId);

    // New methods for random questions
    @Query(value = "SELECT * FROM questions_public WHERE subject = CAST(:subject AS text) AND difficulty = CAST(:difficulty AS text) AND grade = :grade ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<QuestionPublic> findRandomQuestionsBySubject(
        @Param("subject") String subject,
        @Param("difficulty") String difficulty,
        @Param("grade") String grade,
        @Param("limit") int limit
    );

    @Query(value = "SELECT * FROM questions_public WHERE subject = CAST(:subject AS text) AND chapter_id = :chapterId AND difficulty = CAST(:difficulty AS text) AND grade = :grade ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<QuestionPublic> findRandomQuestionsBySubjectAndChapters(
        @Param("subject") String subject,
        @Param("chapterId") String chapterId,
        @Param("difficulty") String difficulty,
        @Param("grade") String grade,
        @Param("limit") int limit
    );

    @Query(value = "SELECT * FROM questions_public WHERE subject = CAST(:subject AS text) AND difficulty = CAST(:difficulty AS text) AND grade = :grade AND is_subscribed = :isSubscribed ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<QuestionPublic> findRandomQuestionsBySubjectAndSubscription(
        @Param("subject") String subject,
        @Param("difficulty") String difficulty,
        @Param("grade") String grade,
        @Param("isSubscribed") Boolean isSubscribed,
        @Param("limit") int limit
    );

    @Query(value = "SELECT * FROM questions_public WHERE subject = CAST(:subject AS text) AND chapter_id = :chapterId AND difficulty = CAST(:difficulty AS text) AND grade = :grade AND is_subscribed = :isSubscribed ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<QuestionPublic> findRandomQuestionsBySubjectAndChaptersAndSubscription(
        @Param("subject") String subject,
        @Param("chapterId") String chapterId,
        @Param("difficulty") String difficulty,
        @Param("grade") String grade,
        @Param("isSubscribed") Boolean isSubscribed,
        @Param("limit") int limit
    );

@Query(value = "SELECT * FROM questions_public WHERE subject = CAST(:subject AS text) AND is_active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
List<QuestionPublic> findFallbackQuestions(
    @Param("subject") String subject,
    @Param("limit") int limit
);

@Query(value = "SELECT * FROM questions_public WHERE subject = CAST(:subject AS text) AND is_active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
List<QuestionPublic> findRandomQuestionsBySubject(
    @Param("subject") String subject,
    @Param("limit") int limit
);


    boolean existsByQuestionText(String questionText);
    
    // Debug method to count available questions
    @Query(value = "SELECT COUNT(*) FROM questions_public WHERE subject = CAST(:subject AS text) AND difficulty = CAST(:difficulty AS text) AND grade = :grade", nativeQuery = true)
    long countBySubjectAndDifficultyAndGrade(
        @Param("subject") String subject,
        @Param("difficulty") String difficulty,
        @Param("grade") String grade
    );
    
    @Query(value = "SELECT COUNT(*) FROM questions_public WHERE subject = CAST(:subject AS text) AND difficulty = CAST(:difficulty AS text) AND grade = :grade AND is_subscribed = :isSubscribed", nativeQuery = true)
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
// JOIN QuestionPublic q ON q.qid = r.questionId
// WHERE r.scheduledExamId = :scheduledExamId
// """)
// List<QuestionReviewDTO> getReviewFromPublic(Long scheduledExamId);



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
FROM QuestionPublic q
WHERE q.qid IN :qids
""")
List<QuestionInsightDTO> fetchPublicQuestions(@Param("qids") List<String> qids);
List<QuestionPublic> findByQidIn(List<String> qids);
// List<QuestionPublic> findByQidInre(List<QuestionPublic> questionsPublic);

} 
