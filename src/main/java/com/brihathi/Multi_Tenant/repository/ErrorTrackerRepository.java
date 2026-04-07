package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.ErrorTracker;
import com.brihathi.Multi_Tenant.dto.ErrorTrackerResponseDTO;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
 
 
import java.util.Collection;
import java.util.List;
 
/**
 * Spring‑Data repository for the <code>error_tracker</code> table.
 */
public interface ErrorTrackerRepository extends JpaRepository<ErrorTracker, Long> {
 
    List<ErrorTracker> findByUserIdAndQidIn(Long userId, Collection<String> qids);
 
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        DELETE FROM ErrorTracker t
        WHERE t.userId = :userId
        AND t.qid IN :qids
    """)
    int deleteByUserIdAndQids(@Param("userId") Long userId, @Param("qids") Collection<String> qids);
 
    List<ErrorTracker> findByUserIdOrderByExamIdDesc(Long userId);
 
    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.ErrorTrackerResponseDTO(
            e.id, e.examId, e.qid,
            e.questionText, e.subject,
            e.answerOption1, e.answerOption2, e.answerOption3, e.answerOption4,
            e.answeredOption, e.correctAnswerOption, e.briefExplanation)
        FROM ErrorTracker e
        WHERE e.userId = :userId
        ORDER BY e.examId DESC
    """)
    List<ErrorTrackerResponseDTO> fetchDtosByUser(@Param("userId") Long userId);
 
    @Query("""
        SELECT new com.brihathi.Multi_Tenant.dto.ErrorTrackerResponseDTO(
            e.id, e.examId, e.qid,
            e.questionText, e.subject,
            e.answerOption1, e.answerOption2, e.answerOption3, e.answerOption4,
            e.answeredOption, e.correctAnswerOption, e.briefExplanation)
        FROM ErrorTracker e
        ORDER BY e.examId DESC
    """)
    List<ErrorTrackerResponseDTO> fetchAllDtos(); 


} 
