package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.dto.ViewTestPerformanceDTO;
import com.brihathi.Multi_Tenant.entity.ExamResult;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
 
@Repository
public interface ViewTestPerformanceRepository extends JpaRepository<ExamResult, Long> {
 
    // Public questions
@Query(value = """
    SELECT q.qid AS questionId,
           q.question_text AS questionText,
           q.answer_option1 AS answerOption1,
           q.answer_option2 AS answerOption2,
           q.answer_option3 AS answerOption3,
           q.answer_option4 AS answerOption4,
           q.brief_explanation AS briefExplanation,
           q.subject AS subject,
           er.answer_option AS answerOption,
           er.correct_answer_option AS correctAnswerOption
    FROM exam_results er
    JOIN public.questions_public q ON er.qid = q.qid
    WHERE er.user_id = :userId
      AND er.exam_id = :examId
""", nativeQuery = true)
List<ViewTestPerformanceDTO> findExamQuestionPublicResults(@Param("userId") Long userId,
                                                           @Param("examId") Long examId);

// Tenant questions
@Query(value = """
    SELECT q.qid AS questionId,
           q.question_text AS questionText,
           q.answer_option1 AS answerOption1,
           q.answer_option2 AS answerOption2,
           q.answer_option3 AS answerOption3,
           q.answer_option4 AS answerOption4,
           q.brief_explanation AS briefExplanation,
           q.subject AS subject,
           er.answer_option AS answerOption,
           er.correct_answer_option AS correctAnswerOption
    FROM exam_results er
    JOIN public.questions_tenant q ON er.qid = q.qid
    WHERE er.user_id = :userId
      AND er.exam_id = :examId
""", nativeQuery = true)
List<ViewTestPerformanceDTO> findExamQuestionTenantResults(@Param("userId") Long userId,
                                                           @Param("examId") Long examId);
}

 
 
 