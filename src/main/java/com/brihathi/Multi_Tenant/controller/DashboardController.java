package com.brihathi.Multi_Tenant.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.brihathi.Multi_Tenant.dto.DifficultyWisePerformanceDTO;
import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
import com.brihathi.Multi_Tenant.dto.TimeAnalysisDTO;
import com.brihathi.Multi_Tenant.dto.ScoreProgressResponseDTO;
import com.brihathi.Multi_Tenant.service.DashboardService;
import com.brihathi.Multi_Tenant.service.SubjectWisePerformanceService;
import com.brihathi.Multi_Tenant.dto.LeadershipBoardTopScoreDTO;
import com.brihathi.Multi_Tenant.dto.PredictedRankDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import com.brihathi.Multi_Tenant.dto.UserDateRangeRequest;
import com.brihathi.Multi_Tenant.dto.UserIdRequest;
import com.brihathi.Multi_Tenant.dto.IndividualSubjectwiseRequest;
import com.brihathi.Multi_Tenant.dto.IndividualChapterwiseRequest;
import com.brihathi.Multi_Tenant.dto.IndividualSubjectwiseResponse;
import com.brihathi.Multi_Tenant.dto.IndividualChapterwiseResponse;
 
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
 
    @Autowired
    private DashboardService dashboardService;
 
    @Autowired
    private SubjectWisePerformanceService subjectWisePerformanceService;
 
    @GetMapping("/difficulty-wise-performance")
    public ResponseEntity<List<DifficultyWisePerformanceDTO>> getDifficultyWisePerformance(@ModelAttribute UserDateRangeRequest request) {
        return ResponseEntity.ok(dashboardService.getDifficultyWisePerformance(request.getUserId(), request.getStartDate(), request.getEndDate()));
    } 
    @GetMapping("/subject-wise-performance")
    public ResponseEntity<List<SubjectWisePerformanceDTO>> getSubjectWisePerformance(@ModelAttribute UserDateRangeRequest request) {
        return ResponseEntity.ok(dashboardService.getSubjectWisePerformance(request.getUserId(), request.getStartDate(), request.getEndDate()));
    }
 
    // @GetMapping("/time-analysis")
    // public ResponseEntity<List<TimeAnalysisDTO>> getTimeAnalysis(@ModelAttribute UserDateRangeRequest request) {
    //     return ResponseEntity.ok(dashboardService.getTimeAnalysis(request.getUserId(), request.getStartDate(), request.getEndDate()));
    // }
   
 
 
    @GetMapping("/time-analysis")
    public ResponseEntity<List<Object[]>> getTimeAnalysis(@ModelAttribute UserDateRangeRequest request) {
        return ResponseEntity.ok(dashboardService.getTimeAnalysis(request.getUserId(), request.getStartDate(), request.getEndDate()));
    }
 
    @GetMapping("/score-progress")
    public ResponseEntity<List<ScoreProgressResponseDTO>> getScoreProgress(@ModelAttribute UserDateRangeRequest request) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return ResponseEntity.ok(dashboardService.getScoreProgress(request.getUserId(), request.getStartDate(), now));
 }
 
   
 
    @GetMapping("/leadership-board")
    public ResponseEntity<List<LeadershipBoardTopScoreDTO>> getTopTotalMarks(@ModelAttribute UserIdRequest request) {
        return ResponseEntity.ok(dashboardService.getTopTotalMarks(request.getUserId()));
    }
 
 
    @GetMapping("/predicted-rank")
    public ResponseEntity<?> getPredictedRank(@RequestParam Long userId) {
        try {
            PredictedRankDTO result = dashboardService.getPredictedRank(userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            if ("NEED TO ATTEMPT FULL EXAM TO KNOW PREDICTED RANK".equals(e.getMessage())) {
                return ResponseEntity.ok(java.util.Map.of("message", "NEED TO ATTEMPT FULL EXAM TO KNOW PREDICTED RANK"));
            }
            return ResponseEntity.status(200).body(java.util.Map.of("error", e.getMessage()));
        }
    }
 
   
 
@GetMapping("/individual_subjectwise")
public ResponseEntity<IndividualSubjectwiseResponse> getIndividualSubjectwise(@ModelAttribute IndividualSubjectwiseRequest request) {
   
    Object data = subjectWisePerformanceService.getIndividualSubjectwiseAnalytics(
        request.getUserId(),
        //request.getDifficultyLevel(),
        request.getSubject(),
        request.getStartDate(),
        request.getEndDate()
    );
    return ResponseEntity.ok(new IndividualSubjectwiseResponse(data));
}
 
@GetMapping("/individual_chapterwise")
public ResponseEntity<IndividualChapterwiseResponse> getIndividualChapterwise(@ModelAttribute IndividualChapterwiseRequest request) {
   
    Object data = subjectWisePerformanceService.getIndividualChapterwiseAnalytics(
        request.getUserId(),
 
        request.getSubject(),
        request.getStartDate(),
        request.getEndDate()
    );
    return ResponseEntity.ok(new IndividualChapterwiseResponse(data));
}
 
}
