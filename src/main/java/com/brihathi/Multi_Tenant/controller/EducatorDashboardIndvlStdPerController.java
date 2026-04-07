// package com.brihathi.Multi_Tenant.controller;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.brihathi.Multi_Tenant.dto.PredictedRankDTO;
// import com.brihathi.Multi_Tenant.dto.ScoreProgressResponseDTO;
// import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
// import com.brihathi.Multi_Tenant.dto.UserDTO;
// import com.brihathi.Multi_Tenant.dto.UserDateRangeRequest;
// import com.brihathi.Multi_Tenant.service.EducatorDashboardIndvlStdPerService;

// import lombok.AllArgsConstructor;
// import lombok.NoArgsConstructor;

// import java.util.List;
// import java.util.Map;

// @RestController
// @AllArgsConstructor
// @RequestMapping("/api/educator/dashboard/indvlStd")
// public class EducatorDashboardIndvlStdPerController {
    
//     private final EducatorDashboardIndvlStdPerService educatorDashboardService;


//     // @GetMapping("/search")
//     // public ResponseEntity<UserDTO> getUser(
//     //         @RequestParam(required = false) String enrollmentId,
//     //         @RequestParam(required = false) String name) {

//     //     if (enrollmentId == null && name == null) {
//     //         throw new IllegalArgumentException("Either enrollmentId or name must be provided");
//     //     }

//     //     UserDTO user = educatorDashboardService.getUserByEnrollmentIdOrName(enrollmentId, name);
//     //     return ResponseEntity.ok(user);
//     // }

//     @GetMapping("/predicted-rank")
//     public ResponseEntity<?> getPredictedRank(@RequestParam Long userId) {
//         try {
//             PredictedRankDTO result = educatorDashboardService.getPredictedRank(userId);
//             if (result == null) {
//                 // Send message when no predicted rank is available
//                 return ResponseEntity.ok(Map.of("message", "NEED TO ATTEMPT FULL EXAM TO KNOW PREDICTED RANK"));
//             }
//             return ResponseEntity.ok(result);
     
//         } catch (RuntimeException e) {
//             return ResponseEntity.ok(Map.of("error", e.getMessage()));
//         }
//     }

//     @GetMapping("/subject-wise-performance")
//     public ResponseEntity<List<SubjectWisePerformanceDTO>> getSubjectWisePerformance(@ModelAttribute UserDateRangeRequest request) {
//         return ResponseEntity.ok(educatorDashboardService.getSubjectWisePerformance(request.getUserId(), request.getStartDate(), request.getEndDate()));
//     }

//     @GetMapping("/score-progress")
//     public ResponseEntity<List<ScoreProgressResponseDTO>> getScoreProgress(@ModelAttribute UserDateRangeRequest request) {
//         java.time.LocalDateTime now = java.time.LocalDateTime.now();
//         return ResponseEntity.ok(educatorDashboardService.getScoreProgress(request.getUserId(), request.getStartDate(), now));
//     }
    

    
// }
