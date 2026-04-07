package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.ExamQuestionsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
 
import java.util.UUID;
 
@Repository
public interface ExamQuestionsReportRepository extends JpaRepository<ExamQuestionsReport, UUID> {
    @Query("SELECT r FROM ExamQuestionsReport r WHERE r.userId = :userId")
List<ExamQuestionsReport> findReportsByUserId(@Param("userId") Long userId);
 
}
 
