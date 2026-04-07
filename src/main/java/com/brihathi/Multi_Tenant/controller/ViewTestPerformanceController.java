package com.brihathi.Multi_Tenant.controller;
 
import com.brihathi.Multi_Tenant.dto.ViewTestPerformanceDTO;
import com.brihathi.Multi_Tenant.service.ViewTestPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api")
public class ViewTestPerformanceController {
 
    @Autowired
    private ViewTestPerformanceService viewTestPerformanceService;
 
    @GetMapping("/view-test-performance/{userId}/{examId}")
    public List<ViewTestPerformanceDTO> getExamResultsForUser(
            @PathVariable Long userId,
            @PathVariable Long examId
    ) {
        return viewTestPerformanceService.getExamQuestionResults(userId, examId);
    }
}
 
 
 