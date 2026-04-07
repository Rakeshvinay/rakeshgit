package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.dto.TenantDashboardDTO;
import com.brihathi.Multi_Tenant.dto.ExamResultsDashboardDTO;
import com.brihathi.Multi_Tenant.dto.StudentResultDetailsDTO;
import com.brihathi.Multi_Tenant.dto.StudentSearchDTO;
import com.brihathi.Multi_Tenant.dto.StudentExamStatsDTO;
import com.brihathi.Multi_Tenant.dto.ScoreProgressResponseDTO;
import com.brihathi.Multi_Tenant.dto.StudentSubjectPerformanceDTO;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.service.EducatorDashboardService;
import com.brihathi.Multi_Tenant.service.ScheduledExamResultSummaryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/educator/dashboard")
@RequiredArgsConstructor
public class EducatorDashboardController {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final EducatorDashboardService dashboardService;
    private final ScheduledExamResultSummaryService scheduledExamResultSummaryService;

    @GetMapping("/structure")
    public ResponseEntity<TenantDashboardDTO> getDashboardStructure(
            HttpServletRequest request
    ) {

        /* ===================== AUTH ===================== */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        Object principal = auth.getPrincipal();

if (!(principal instanceof com.brihathi.Multi_Tenant.entity.Educator educator)) {
    throw new RuntimeException("Access Denied: Educator login required");
}


        /* ===================== TENANT ===================== */
        String subdomain = extractSubdomain(request);

        /* 🚀 SERVICE */
        TenantDashboardDTO response =
                dashboardService.getDashboardStructure(subdomain);

        return ResponseEntity.ok(response);
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


    @GetMapping("/results/{eduExamId}")
public ResponseEntity<ExamResultsDashboardDTO> getResults(
        @PathVariable Long eduExamId,
        HttpServletRequest request) {

    String subdomain = extractSubdomain(request);
    return ResponseEntity.ok(
        dashboardService.getExamResults(eduExamId)
    );
}

@GetMapping("/results/details/{scheduledExamId}")
public ResponseEntity<StudentResultDetailsDTO> getResultDetails(
        @PathVariable Long scheduledExamId,
        HttpServletRequest request
) {
    String subdomain = extractSubdomain(request);
    return ResponseEntity.ok(
            dashboardService.getStudentResultDetails(scheduledExamId, subdomain)
    );
}


// @GetMapping("/students/search")
// public ResponseEntity<List<StudentSearchDTO>> searchStudents(
//         @RequestParam(required = false) String search,
//         HttpServletRequest request
// ) {
//     String subdomain = extractSubdomain(request);
//     return ResponseEntity.ok(
//             dashboardService.getStudentsForSearch(subdomain, search)
//     );
// }
@GetMapping("/students/search")
public List<StudentSearchDTO> searchStudents(
        @RequestParam(required = false) String search,
        HttpServletRequest request) {

    String subdomain = extractSubdomain(request);

    Long tenantId = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"))
            .getTenantId();

    // 🔹 Normalize
    if (search != null && search.trim().isEmpty()) {
        search = null;
    }

    return userRepository.searchStudents(tenantId, search);
}



@GetMapping("/student/exam-stats/{userId}")
public ResponseEntity<StudentExamStatsDTO> getStudentStats(
        @PathVariable Long userId,
        HttpServletRequest request) {
            String subdomain = extractSubdomain(request);
    return ResponseEntity.ok(
            dashboardService.getStudentExamStats(userId, subdomain)
    );
}


@GetMapping("/student/subject-performance/{userId}")
public List<StudentSubjectPerformanceDTO> getSubjectPerformance(
        @PathVariable Long userId,
        HttpServletRequest request) {
            String subdomain = extractSubdomain(request);
    return dashboardService.getSubjectWisePerformance(userId, subdomain);
}



@GetMapping("/student/score-progress/{userId}")
public List<ScoreProgressResponseDTO> getScoreProgress(
        @PathVariable Long userId,
        HttpServletRequest request) {
            String subdomain = extractSubdomain(request);

    return dashboardService.getScoreProgress(userId, subdomain);
}

}
