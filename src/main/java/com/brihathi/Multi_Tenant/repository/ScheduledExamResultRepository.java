package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.brihathi.Multi_Tenant.dto.QuestionReviewDTO;

@Repository
public interface ScheduledExamResultRepository
        extends JpaRepository<ScheduledExamResult, Long> {

    List<ScheduledExamResult> findByScheduledExamId(Long scheduledExamId);

    List<ScheduledExamResult> findByScheduledExamIdAndQid( Long scheduledExamId, String qId);
    List<ScheduledExamResult> findByEduScheduledExamIdAndUserId(Long eduScheduledExamId,Long userId);
    List<ScheduledExamResult> findByScheduledExamIdAndUserId(Long scheduledExamId,Long userId);


    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.QuestionReviewDTO(
            r.qid,
            r.answerOption,
            q.questionText,
            q.correctAnswerOption,
            q.answerOption1,
            q.answerOption2,
            q.answerOption3,
            q.answerOption4,
            q.briefExplanation,
            q.subject
        )
        FROM ScheduledExamResult r
        JOIN QuestionPublic q ON q.qid = r.qid
        WHERE r.scheduledExamId = :scheduledExamId
        """)
        List<QuestionReviewDTO> getReviewFromPublic(Long scheduledExamId);
        

        @Query("""
            SELECT new com.brihathi.Multi_Tenant.dto.QuestionReviewDTO(
                r.qid,
                r.answerOption,
                q.questionText,
                q.correctAnswerOption,
                q.answerOption1,
                q.answerOption2,
                q.answerOption3,
                q.answerOption4,
                q.briefExplanation,
                q.subject
            )
            FROM ScheduledExamResult r
            JOIN QuestionTenant q ON q.qid = r.qid
            WHERE r.scheduledExamId = :scheduledExamId
            """)
            List<QuestionReviewDTO> getReviewFromTenant(Long scheduledExamId);
            
}

