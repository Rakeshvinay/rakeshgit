package com.brihathi.Multi_Tenant.controller; 

import com.brihathi.Multi_Tenant.dto.RetryExamRequestDTO;
import com.brihathi.Multi_Tenant.service.RetryTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import org.springframework.http.ResponseEntity;
 
import java.util.*;
 
@RestController
@RequestMapping("/api")
public class RetryTestController {
 
    @Autowired
    private RetryTestService retryTestService;
 
    @GetMapping("/retry-test/{userId}/{examId}")
    public List<Map<String, Object>> getExamResultsForUser(
            @PathVariable Long userId,
            @PathVariable Long examId
    ) {
        return retryTestService.getExamQuestionResults(userId, examId);
    }


//     @GetMapping("/retry-test/{userId}/{examId}")
// public Map<String, Object> getExamResultsForUser(
//         @PathVariable Long userId,
//         @PathVariable Long examId
// ) {
//     return retryTestService.getExamQuestionResults(userId, examId);
// }



    @PostMapping("/create-retry-test")
    public ResponseEntity<Map<String, Object>> createAndStartRetryExam(@RequestBody RetryExamRequestDTO request) {
        List<Object> questions = new ArrayList<>();
        if (request.getQuestionsPublic() != null) {
            questions.addAll(request.getQuestionsPublic());
        }
        if (request.getQuestionTenant() != null) {
            questions.addAll(request.getQuestionTenant());
        }
        
        Map<String, Object> response = retryTestService.createAndStartRetryExam(
                request.getUserId(),
                request.getSubject(),
                request.getChapterIds(),
                request.getDifficulty(),
                request.getGrade(),
                questions
        );
        return ResponseEntity.ok(response);
    }
}
 
 
 