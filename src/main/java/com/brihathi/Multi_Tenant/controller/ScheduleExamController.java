package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.dto.CreateScheduledExamRequest;
import com.brihathi.Multi_Tenant.dto.AllScheduleExamsResDTO;
import com.brihathi.Multi_Tenant.dto.ExamOverviewDTO;
import com.brihathi.Multi_Tenant.dto.EducatorExamListDTO;
import com.brihathi.Multi_Tenant.entity.ScheduledExam;
import com.brihathi.Multi_Tenant.entity.EducatorScheduledExam;
import com.brihathi.Multi_Tenant.dto.ScheduleExamResponse;
import com.brihathi.Multi_Tenant.dto.StartScheduledExamResponse;
import com.brihathi.Multi_Tenant.dto.UpdateScheduledExamRequest;
import com.brihathi.Multi_Tenant.service.ScheduleExamService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/schedule_exams")
@RequiredArgsConstructor
public class ScheduleExamController {

    private final ScheduleExamService scheduleExamService;

    @PostMapping("/create")
    public ResponseEntity<ScheduleExamResponse> createScheduledExam(
            @RequestBody CreateScheduledExamRequest request,
            HttpServletRequest httpServletRequest
    ) {

        // 🔹 Extract subdomain
        String subdomain = extractSubdomain(httpServletRequest);

        ScheduleExamResponse response =
                scheduleExamService.createScheduledExam(request, subdomain);

        return ResponseEntity.ok(response);
    }

    /**
     * Example:
     *  brihathi.neetswan.ai → brihathi
     */
    private String extractSubdomain(HttpServletRequest request) {

        String host = request.getServerName(); 
        // examples:
        // brihathi.localhost
        // brihathi.neetswan.ai
    
        // ✅ Local development support
        if (host.endsWith("localhost")) {
            return host.split("\\.")[0]; // brihathi
        }
    
        // ✅ Production subdomain support
        String[] parts = host.split("\\.");
    
        if (parts.length < 3) {
            throw new RuntimeException("Invalid tenant subdomain: " + host);
        }
    
        return parts[0];
    }
    
    @PostMapping("/{userId}/start/{scheduledExamId}")
    public ResponseEntity<StartScheduledExamResponse> startScheduledExam(
            @PathVariable Long userId,
            @PathVariable Long scheduledExamId
    ) {
        StartScheduledExamResponse response =
                scheduleExamService.startScheduledExam(userId, scheduledExamId);

        return ResponseEntity.ok(response);
    }




    @GetMapping("/{userId}/generated-qids/{scheduledExamId}")
    public ResponseEntity<Map<String, Object>> getGeneratedQids(
            @PathVariable Long userId,
            @PathVariable Long scheduledExamId) {

        Map<String, Object> response =
                scheduleExamService.getGeneratedQids(userId, scheduledExamId);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{userId}/update-question/{scheduledExamId}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long userId,
            @PathVariable Long scheduledExamId,
            @RequestBody Map<String, Object> questionUpdate) {

        Map<String, Object> response =
                scheduleExamService.updateQuestion(userId, scheduledExamId, questionUpdate);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{userId}/end/{scheduledExamId}")
    public ResponseEntity<Map<String, Object>> endExam(
            @PathVariable Long userId,
            @PathVariable Long scheduledExamId) {

        return ResponseEntity.ok(
                scheduleExamService.endScheduledExam(userId, scheduledExamId)
        );
    }

    @GetMapping("/questions/{qid}")
    public ResponseEntity<Map<String, Object>> getQuestionByQid(
            @PathVariable String qid,
            HttpServletRequest request) {
    
        String host = request.getServerName(); // brihathi.localhost
        String subdomain = host.split("\\.")[0];
    
        return ResponseEntity.ok(
                scheduleExamService.getQuestionByQid(qid, subdomain)
        );
    }
    @PostMapping("/{userId}/abort/{scheduledExamId}")
    public ResponseEntity<ScheduledExam> abortScheduledExam(
            @PathVariable Long userId,
            @PathVariable Long scheduledExamId) {

        return ResponseEntity.ok(
                scheduleExamService.abortScheduledExam(userId, scheduledExamId)
        );
    }


    @PostMapping("/{userId}/confirm-submission/{scheduledExamId}")
    public ResponseEntity<Map<String, Object>> confirmSubmission(
            @PathVariable Long userId,
            @PathVariable Long scheduledExamId) {

        return ResponseEntity.ok(
                scheduleExamService.confirmSubmission(userId, scheduledExamId)
        );
    }



    @GetMapping("/{userId}/final-results/{scheduledExamId}")
public ResponseEntity<?> getFinalResults(
        @PathVariable Long userId,
        @PathVariable Long scheduledExamId
) {
    return ResponseEntity.ok(
            scheduleExamService.getFinalResults(userId, scheduledExamId)
    );
}

@GetMapping("/{userId}/all")
public ResponseEntity<List<AllScheduleExamsResDTO>> getAllScheduledExams(
        @PathVariable Long userId
) {
    return ResponseEntity.ok(
        scheduleExamService.getAllScheduledExams(userId)
    );
}



@GetMapping("/scheduled")
public ResponseEntity<List<EducatorExamListDTO>> getScheduledExams(
        @RequestParam(required = false) String branch,
        @RequestParam(required = false) String batch,
        @RequestParam(required = false) String examStatus,
        HttpServletRequest httpServletRequest
    ) {
        String subdomain = extractSubdomain(httpServletRequest);
        return ResponseEntity.ok(
                scheduleExamService.findScheduledExamsByFilters(
                        subdomain,
                        branch,
                        batch,
                        examStatus)
        );
    }


    @GetMapping("/scheduled/overview")
    public ResponseEntity<ExamOverviewDTO> getExamOverview(
            HttpServletRequest request
    ) {
        String subdomain = extractSubdomain(request);
        return ResponseEntity.ok(scheduleExamService.getExamOverview(subdomain));
    }
    

    


    @GetMapping("/{scheduledExamId}")
    public ResponseEntity<?> getScheduledExamById(@PathVariable Long scheduledExamId) {
        return ResponseEntity.ok(
            scheduleExamService.getScheduledExamById(scheduledExamId)
                .orElseThrow(() -> new RuntimeException("Scheduled exam not found"))
        );
    }


    @PostMapping("/cancel/{eduScheduledExamId}")
public ResponseEntity<String> cancelScheduledExam(
        @PathVariable Long eduScheduledExamId,
        HttpServletRequest request
) {
    String subdomain = extractSubdomain(request);
    scheduleExamService.cancelScheduledExam(eduScheduledExamId, subdomain);
    return ResponseEntity.ok("Scheduled exam cancelled successfully");
}

@PutMapping("/update/{eduScheduledExamId}")
public ResponseEntity<String> updateScheduledExam(
        @PathVariable Long eduScheduledExamId,
        @RequestBody UpdateScheduledExamRequest request,
        HttpServletRequest httpServletRequest
) {
    String subdomain = extractSubdomain(httpServletRequest);

    scheduleExamService.updateScheduledExam(
            eduScheduledExamId,
            request,
            subdomain
    );

    return ResponseEntity.ok("Scheduled exam updated successfully");
}


}
