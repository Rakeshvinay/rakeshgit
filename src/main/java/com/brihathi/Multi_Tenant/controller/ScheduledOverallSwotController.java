package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.service.ScheduledOverallSwotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import com.brihathi.Multi_Tenant.dto.SwotResponseDTO;
import jakarta.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/api/educator/swot")
@RequiredArgsConstructor
public class ScheduledOverallSwotController {

    private final ScheduledOverallSwotService scheduledOverallSwotService;

    /**
     * VIEW OVERALL SWOT DETAILS (Scheduled Exam Analytics)
     *
     * Example:
     * GET /api/student/swot/view/5
     * Header: subdomain = brihathi
     */
    @GetMapping("/view/{userId}/overallswot")
    public SwotResponseDTO viewSwotDetails(
            @PathVariable Long userId,
            HttpServletRequest request) {
                String subdomain = extractSubdomain(request);
        return scheduledOverallSwotService.viewSwotDetails(userId, subdomain);
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
}
