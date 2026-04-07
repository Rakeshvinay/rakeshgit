package com.brihathi.Multi_Tenant.controller;


import com.brihathi.Multi_Tenant.service.SubjectAnalyticsService;
import com.brihathi.Multi_Tenant.service.QuestionInsightService;
import com.brihathi.Multi_Tenant.service.ScheduledChapterAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import com.brihathi.Multi_Tenant.dto.PerformanceDistributionDTO;
import com.brihathi.Multi_Tenant.dto.SubjectPerformanceDTO;
import com.brihathi.Multi_Tenant.dto.PerformanceDistributionResponseDTO;
import com.brihathi.Multi_Tenant.dto.ChapterPerformanceAnalyticsDTO;
import com.brihathi.Multi_Tenant.dto.ChapterAnalyticsResponseDTO;
import com.brihathi.Multi_Tenant.dto. QuestionInsightResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class StudentAnalyticsController {

    private final SubjectAnalyticsService analyticsService;
    private final ScheduledChapterAnalyticsService service;
    private final QuestionInsightService questionService;

    @GetMapping("/performance-distribution")
    public PerformanceDistributionResponseDTO getDistribution(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String batch,
            HttpServletRequest request
    ) {
        String subdomain = extractSubdomain(request);
        return analyticsService.getPerformanceDistribution(subdomain, branch, batch);
    }
    

     /**
     * brihathi.neetswan.ai → brihathi
     */
     private String extractSubdomain(HttpServletRequest request) {

        String host = request.getServerName();

        // Local
        if (host.endsWith("localhost")) {
            return host.split("\\.")[0];
        }

        // Production
        String[] parts = host.split("\\.");

        if (parts.length < 3) {
            throw new RuntimeException("Invalid tenant subdomain: " + host);
        }

        return parts[0];
    }


    @GetMapping("/subject-performance")
    public List<SubjectPerformanceDTO> getSubjectPerformance(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String batch,
            HttpServletRequest request) {

        String subdomain = extractSubdomain(request);
        return analyticsService.getSubjectPerformance(subdomain, branch, batch);
    }


    @GetMapping("/chapter-performance")
    public ChapterAnalyticsResponseDTO getChapterPerformance(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String subject,
            HttpServletRequest request) {
    
        String subdomain = extractSubdomain(request);
    
        return service.getChapterPerformance(subdomain, branch, batch, subject);
    }
    


    @GetMapping("/question-insights")
    public QuestionInsightResponseDTO getInsights(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String subject,
            HttpServletRequest request) {

        return questionService.getInsights(branch, batch, subject, request);
    }
}
