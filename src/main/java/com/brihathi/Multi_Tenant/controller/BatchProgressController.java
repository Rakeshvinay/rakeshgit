package com.brihathi.Multi_Tenant.controller;
 
import com.brihathi.Multi_Tenant.dto.BatchProgressSummaryDTO;
import com.brihathi.Multi_Tenant.dto.StudentRiskDTO;
import com.brihathi.Multi_Tenant.dto.BatchAttendanceResponseDTO;
import com.brihathi.Multi_Tenant.service.BatchProgressService;
import com.brihathi.Multi_Tenant.dto.BatchAIInsightDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
 
@RestController
@RequestMapping("/api/analytics/batch-progress")
@RequiredArgsConstructor
public class BatchProgressController {
 
    private final BatchProgressService service;
 
    @GetMapping("/batch-overview")
    public List<BatchProgressSummaryDTO> getBatchProgress(
        HttpServletRequest request,
            @RequestParam String branch,
            @RequestParam(required = false) String batch
    ) {
 
        String subdomain = extractSubdomain(request);
        return service.getBatchProgress(subdomain,branch, batch);
    }
 
    @GetMapping("/attendance-trend")
    public List<BatchAttendanceResponseDTO> getTrend(
        HttpServletRequest request,
            @RequestParam String branch,
            @RequestParam(required = false) String batch
    ) {
        String subdomain = extractSubdomain(request);
       
         return   service.getBatchAttendance(subdomain,branch, batch);
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
 
    @GetMapping("/student-leastanalysis")
public Map<String, List<StudentRiskDTO>> getRisk(
        HttpServletRequest request,
        @RequestParam String branch,
        @RequestParam String batch) {
 
    String subdomain = extractSubdomain(request);
    return service.getStudentRisk(subdomain, branch, batch);
}
 
 
@GetMapping("/batch-ai-insights")
public List<BatchAIInsightDTO> getAIInsights(
        HttpServletRequest request,
        @RequestParam String branch,
        @RequestParam String batch
) {
    String subdomain = extractSubdomain(request);
    return service.getBatchAIInsights(subdomain, branch, batch);
}
 
 
}
 
 