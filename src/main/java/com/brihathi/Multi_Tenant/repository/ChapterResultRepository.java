package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.ChapterResult;
import com.brihathi.Multi_Tenant.enums.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import com.brihathi.Multi_Tenant.entity.ChapterWiseResult; 
import java.util.List;
 
@Repository
public interface ChapterResultRepository extends JpaRepository<ChapterResult, Long> {
    List<ChapterResult> findByUserIdAndExamType(Long userId, Subject examType);
    List<ChapterResult> findByUserId(Long userId);

    // @Query("SELECT cwr FROM ChapterWiseResult cwr WHERE cwr.user.userId = :userId AND cwr.exam.examId = :examId")
    // java.util.List<ChapterWiseResult> findByUser_UserIdAndExam_ExamId(Long userId, Long examId); 
 
    @Query("""
        SELECT COUNT(DISTINCT cr.examId)
        FROM ChapterResult cr
        WHERE cr.userId = :userId
        AND cr.examId IS NOT NULL
        AND cr.marks IS NOT NULL
    """)
    int countDistinctExamIdsByUserId(@Param("userId") Long userId);
 
    @Query("""
        SELECT DISTINCT cr.examId
        FROM ChapterResult cr
        WHERE cr.userId = :userId
        AND cr.examId IS NOT NULL
        AND cr.marks IS NOT NULL
    """)
    List<Long> findDistinctExamIdsByUserId(@Param("userId") Long userId);

    List<ChapterResult> findByExamId(Long examId);
}
