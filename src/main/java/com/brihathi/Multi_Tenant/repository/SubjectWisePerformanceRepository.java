package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.SubjectWisePerformanceBar;
import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
 
@Repository
public interface SubjectWisePerformanceRepository extends JpaRepository<SubjectWisePerformanceBar, Long> {
 
    @Query(value = """
        SELECT
            subject AS subject,
            SUM(percentage) AS totalPercentage,
            COUNT(*) AS countRows,
            ROUND(SUM(percentage) / COUNT(*)) AS averagePercentage
        FROM  
            subject_wise_performance_bar
        WHERE
            user_id = :userId
            AND created_at BETWEEN :startDate AND :endDate
        GROUP BY
            subject
        ORDER BY
            averagePercentage DESC
        """, nativeQuery = true)
    List<SubjectWisePerformanceDTO> getSubjectWisePerformance(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
   
}
