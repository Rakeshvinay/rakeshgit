package com.brihathi.Multi_Tenant.controller;
 
import com.brihathi.Multi_Tenant.dto.ExamQuestionsReportRequestDTO;
import com.brihathi.Multi_Tenant.service.ExamQuestionsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.brihathi.Multi_Tenant.entity.ExamQuestionsReport;
import org.springframework.web.bind.annotation.*;
import java.util.List;
 
@RestController
@RequestMapping("/api/exam-reports")
public class ExamQuestionsReportController {
 
    @Autowired
    private ExamQuestionsReportService reportService;
 
    @PostMapping
    public ResponseEntity<String> saveReport(@RequestBody ExamQuestionsReportRequestDTO dto) {
        reportService.saveReport(dto);
        return ResponseEntity.ok("Report saved successfully");
    }
 
 
    // Retrieve all reports for a specific user using JPQL
@GetMapping("/{userId}/all")
public ResponseEntity<List<ExamQuestionsReport>> getAllReportsByUserId(@PathVariable Long userId) {
    List<ExamQuestionsReport> reports = reportService.getfindReportsByUserId(userId);
    return ResponseEntity.ok(reports);
}
 
}
