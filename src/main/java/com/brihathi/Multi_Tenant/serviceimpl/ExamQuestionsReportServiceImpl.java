package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.dto.ExamQuestionsReportRequestDTO;
import com.brihathi.Multi_Tenant.entity.ExamQuestionsReport;
import com.brihathi.Multi_Tenant.enums.ReportValidated;
 
import com.brihathi.Multi_Tenant.repository.ExamQuestionsReportRepository;
import com.brihathi.Multi_Tenant.service.ExamQuestionsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.*;
import java.util.UUID;
 
@Service
public class ExamQuestionsReportServiceImpl implements ExamQuestionsReportService {
 
    @Autowired
    private ExamQuestionsReportRepository reportRepository;
 
    @Override
    public void saveReport(ExamQuestionsReportRequestDTO dto) {
        
        ExamQuestionsReport report = ExamQuestionsReport.builder()
                .uuid(UUID.randomUUID())
                .userId(dto.getUserId())
                .tenantId(dto.getTenantId())
                .qId(dto.getQId())
                .questionText(dto.getQuestionText())
                .answeredOption(dto.getAnsweredOption())
                .correctAnswerOption(dto.getCorrectAnswerOption())
                .report(dto.getReport())
                .answerOption1(dto.getAnswerOption1())
                .answerOption2(dto.getAnswerOption2())
                .answerOption3(dto.getAnswerOption3())
                .answerOption4(dto.getAnswerOption4())
                .isValidated(ReportValidated.UNDER_REVIEW)
                .actionsTaken(null)
                .build();
 
        reportRepository.save(report);
    }
//    @Override
//     public List<ExamQuestionsReport> getAllReports() {
//         return reportRepository.findAllReports();
//     }
@Override
public List<ExamQuestionsReport> getfindReportsByUserId(Long userId) {
    return reportRepository.findReportsByUserId(userId);
}
 
 
}
 
