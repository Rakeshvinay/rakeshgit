package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ExamResultsSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.*;

@Repository
public interface ExamResultsSummaryRepository extends JpaRepository<ExamResultsSummary, Long> {
    Optional<ExamResultsSummary> findByUser_UserIdAndExam_ExamId(Long userId, Long examId);
    List<ExamResultsSummary> findAllByUser_UserIdAndExam_ExamId(Long userId, Long examId);
 
} 
