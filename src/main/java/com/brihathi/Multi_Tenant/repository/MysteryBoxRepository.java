package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.dto.MysteryBoxResponseDTO;
import com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO;
import com.brihathi.Multi_Tenant.entity.MysteryBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
 
//import jakarta.transaction.Transactional;
 
import java.util.*;
 
@Repository
public interface MysteryBoxRepository extends JpaRepository<MysteryBox, Long> {
 
    // Fetch unattempted questions to insert into mystery box
    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.MysteryBoxResponseDTO(
            NULL, er.examId, q.qid, q.questionText, q.subject,
            q.answerOption1, q.answerOption2, q.answerOption3, q.answerOption4,
            q.correctAnswerOption, q.briefExplanation
        )
        FROM ExamResult er
        JOIN QuestionPublic q ON er.qid = q.qid
        WHERE er.userId = :userId
          AND er.examId = :examId
          AND er.validateAnswer IS NULL
    """)
    List<MysteryBoxResponseDTO> findUnattemptedQuestionsPublic(
        @Param("userId") Long userId,
        @Param("examId") Long examId
    );
    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.MysteryBoxResponseDTO(
            NULL, er.examId, q.qid, q.questionText, q.subject,
            q.answerOption1, q.answerOption2, q.answerOption3, q.answerOption4,
            q.correctAnswerOption, q.briefExplanation
        )
        FROM ExamResult er
        JOIN QuestionTenant q ON er.qid = q.qid
        WHERE er.userId = :userId
          AND er.examId = :examId
          AND er.validateAnswer IS NULL
    """)
    List<MysteryBoxResponseDTO> findUnattemptedQuestionsTenant(
        @Param("userId") Long userId,
        @Param("examId") Long examId
    );
       
 
 
    // Fetch mystery box questions for a user
    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.MysteryBoxResponseDTO(
            m.id, m.examId, m.qid,
            m.questionText, m.subject,
            m.answerOption1, m.answerOption2, m.answerOption3, m.answerOption4,
            m.correctAnswerOption, m.briefExplanation
        )
        FROM MysteryBox m
        WHERE m.userId = :userId
        ORDER BY m.examId DESC
    """)
    List<MysteryBoxResponseDTO> fetchDtosByUser(@Param("userId") Long userId);
 
 
    // Check if a question already exists in the mystery box
    boolean existsByUserIdAndExamIdAndQid(Long userId, Long examId, String qid);
 
 
    // ✅ Delete questions from mystery box that have now been attempted
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    //@Transactional
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    // @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM MysteryBox m
        WHERE m.userId = :userId
          AND m.tenantId = :tenantId
          AND m.qid IN :qids
    """)
    int deleteByUserTenantAndQids(
            @Param("userId") Long userId,
            @Param("tenantId") Long tenantId,
            @Param("qids") List<String> qids
    );
   
 
 
    // ✅ Count deleted mystery box questions by subject
    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO(m.subject, COUNT(m))
        FROM MysteryBox m
        WHERE m.userId = :userId
          AND m.qid IN (
            SELECT r.qid FROM ExamResult r
            WHERE r.userId = :userId
              AND r.examId = :examId
              AND r.validateAnswer IS NOT NULL
          )
        GROUP BY m.subject
    """)
    List<SubjectQuestionCountDTO> countQuestionsToDeleteFromMysteryBoxBySubject(
            @Param("userId") Long userId,
            @Param("examId") Long examId
    );
 
   
 
     // ✅ Count new questions to add into mystery box by subject
     @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO(q.subject, COUNT(q))
        FROM QuestionPublic q
        WHERE q.qid IN (
            SELECT r.qid FROM ExamResult r
            WHERE r.userId = :userId
              AND r.examId = :examId
              AND r.validateAnswer IS NULL
        )
        AND q.qid NOT IN (
            SELECT m.qid FROM MysteryBox m WHERE m.userId = :userId
        )
        GROUP BY q.subject
        """)
        List<SubjectQuestionCountDTO> countNewQuestionsPublic(Long userId, Long examId);
        @Query("""
            SELECT new com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO(q.subject, COUNT(q))
            FROM QuestionTenant q
            WHERE q.qid IN (
                SELECT r.qid FROM ExamResult r
                WHERE r.userId = :userId
                  AND r.examId = :examId
                  AND r.validateAnswer IS NULL
            )
            AND q.qid NOT IN (
                SELECT m.qid FROM MysteryBox m WHERE m.userId = :userId
            )
            GROUP BY q.subject
            """)
            List<SubjectQuestionCountDTO> countNewQuestionsTenant(Long userId, Long examId);
                   
 
     // ✅ 7. NEW: Fetch all existing qids for a user to avoid duplicates
     @Query("""
        SELECT m.qid FROM MysteryBox m WHERE m.userId = :userId AND m.tenantId = :tenantId
    """)
    Set<String> findQidsByUserAndTenant(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
 