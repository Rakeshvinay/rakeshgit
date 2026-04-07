package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.dto.TenantDashboardDTO;
import com.brihathi.Multi_Tenant.dto.StudentSearchDTO;
import com.brihathi.Multi_Tenant.dto.StudentResultDetailsDTO;
import com.brihathi.Multi_Tenant.dto.ExamResultsDashboardDTO;
import com.brihathi.Multi_Tenant.dto.StudentExamStatsDTO;
import com.brihathi.Multi_Tenant.dto.StudentSubjectPerformanceDTO;
import com.brihathi.Multi_Tenant.dto.ScoreProgressDTO;
import com.brihathi.Multi_Tenant.dto.ScoreProgressResponseDTO;

import java.util.List;

public interface EducatorDashboardService {
    TenantDashboardDTO getDashboardStructure(String subdomain);


    ExamResultsDashboardDTO getExamResults(Long eduExamId);


    StudentResultDetailsDTO getStudentResultDetails(
        Long scheduledExamId,
        String subdomain
);

List<StudentSearchDTO> getStudentsForSearch(String subdomain, String search);

StudentExamStatsDTO getStudentExamStats(Long userId, String subdomain);

List<StudentSubjectPerformanceDTO> getSubjectWisePerformance(Long userId, String subdomain);

List<ScoreProgressResponseDTO> getScoreProgress(Long userId, String subdomain);
}
