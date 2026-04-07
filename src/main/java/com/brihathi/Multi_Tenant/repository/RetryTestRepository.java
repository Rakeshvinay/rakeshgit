package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.dto.RetryTestDTO;
import com.brihathi.Multi_Tenant.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetryTestRepository extends JpaRepository<ExamResult, Long> {

    // @Query(value = """
    //     SELECT
    //         q.qid AS questionId,
    //         q.question_text AS questionText,
    //         q.answer_option1 AS answerOption1,
    //         q.answer_option2 AS answerOption2,
    //         q.answer_option3 AS answerOption3,
    //         q.answer_option4 AS answerOption4,
    //         q.subject AS subject
    //     FROM examresults er
    //     JOIN questions q ON er.qid = q.qid
    //     WHERE er.user_id = :userId AND er.exam_id = :examId
    //     """, nativeQuery = true)
    // List<RetryTestDTO> findExamQuestion(@Param("userId") Long userId, @Param("examId") Long examId);

    

    @Query(value = """
        SELECT
            q.qid AS questionId,
            q.question_text AS questionText,
            q.answer_option1 AS answerOption1,
            q.answer_option2 AS answerOption2,
            q.answer_option3 AS answerOption3,
            q.answer_option4 AS answerOption4,
            q.correct_answer_option,
            e.exam_type AS subject,
            e.chapter_id AS chapterId,
            e.difficulty AS difficulty,
            e.grade AS grade
        FROM exam_results er
        JOIN questions_public q ON er.qid = q.qid
        JOIN exams e ON er.exam_id = e.exam_id
        WHERE er.user_id = :userId AND er.exam_id = :examId
        UNION ALL
        SELECT
            q.qid AS questionId,
            q.question_text AS questionText,
            q.answer_option1 AS answerOption1,
            q.answer_option2 AS answerOption2,
            q.answer_option3 AS answerOption3,
            q.answer_option4 AS answerOption4,
            q.correct_answer_option,
            e.exam_type AS subject,
            e.chapter_id AS chapterId,
            e.difficulty AS difficulty,
            e.grade AS grade
        FROM exam_results er
        JOIN questions_tenant q ON er.qid = q.qid
        JOIN exams e ON er.exam_id = e.exam_id
        WHERE er.user_id = :userId AND er.exam_id = :examId
    """, nativeQuery = true)
    List<RetryTestDTO> findExamQuestion(@Param("userId") Long userId, @Param("examId") Long examId);

}


