
package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.ExamFinalResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
 
@Repository
public interface ExamFinalResultRepository extends JpaRepository<ExamFinalResult, Long> {
    @Query("SELECT e FROM ExamFinalResult e JOIN e.chapters c WHERE e.examId = :examId AND c.userId = :userId")
    Optional<ExamFinalResult> findByExamIdAndUserId(@Param("examId") Long examId, @Param("userId") Long userId);

    Optional<ExamFinalResult> findByExamId(Long examId);
}    
 