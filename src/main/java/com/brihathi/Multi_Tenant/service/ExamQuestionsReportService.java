package com.brihathi.Multi_Tenant.service;
import java.util.List;
import com.brihathi.Multi_Tenant.entity.ExamQuestionsReport;
 
import com.brihathi.Multi_Tenant.dto.ExamQuestionsReportRequestDTO;
 
public interface ExamQuestionsReportService {
    void saveReport(ExamQuestionsReportRequestDTO dto);
    //List<ExamQuestionsReport> getAllReports();
    List<ExamQuestionsReport> getfindReportsByUserId(Long userId);
}
