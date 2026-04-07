package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.service.ExamService;
import com.brihathi.Multi_Tenant.dto.AbortExamResponseDTO;
import com.brihathi.Multi_Tenant.dto.CreateNormalExamRequest;
import com.brihathi.Multi_Tenant.dto.NormalExamResponse;
import com.brihathi.Multi_Tenant.dto.StartExamResponseDTO;
import com.brihathi.Multi_Tenant.dto.EndExamSummaryDTO;
import com.brihathi.Multi_Tenant.dto.UpdateQuestionRequestDTO;
import com.brihathi.Multi_Tenant.dto.ConfirmSubmissionResponseDTO;
import com.brihathi.Multi_Tenant.dto.QuestionStatusDTO;
// import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
import com.brihathi.Multi_Tenant.repository.QuestionPublicRepository;
import com.brihathi.Multi_Tenant.repository.QuestionTenantRepository;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
// import com.brihathi.Multi_Tenant.service.RedisTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import jakarta.servlet.http.HttpServletRequest;



import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;
 
@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);
    private final ExamService examService;
    // private final ExamResultRepository examResultRepository;
    // private final RedisTestService redisExamResultService;
    // private final QuestionRepository questionRepository;
 
    @Autowired
    public ExamController(ExamService examService,
                        //  ExamResultRepository examResultRepository,
                        //  RedisTestService redisExamResultService,
                         QuestionPublicRepository questionPublicRepository,
                        QuestionTenantRepository questionTenantRepository) {
        this.examService = examService;
        // this.examResultRepository = examResultRepository;
        // this.redisExamResultService = redisExamResultService;
        // this.questionRepository = questionRepository;
    }


    @GetMapping("/getexaminfo")
    public ResponseEntity<?> getExamInfo(HttpServletRequest request) {

        String host = request.getHeader("Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }

        String subdomain = extractSubdomain(host);
        logger.info("ExamInfo requested for subdomain={}", subdomain);

        return ResponseEntity.ok(examService.getExamInfo(subdomain));
    }

    private static String extractSubdomain(String host) {
        if (host == null) return "";
        String h = host.split(":")[0];
        String[] parts = h.split("\\.");
        return parts.length > 1 ? parts[0] : parts[0];
    }


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


@PostMapping("/create")
public ResponseEntity<NormalExamResponse> createNormalExam(
        @RequestBody CreateNormalExamRequest request,
        HttpServletRequest httpServletRequest
) {
    String subdomain = extractSubdomain(httpServletRequest);
    return ResponseEntity.ok(
            examService.createNormalExam(request, subdomain)
    );
}



@PostMapping("/{examId}/start")
public ResponseEntity<StartExamResponseDTO> startNormalExam(
        @PathVariable Long examId,
        HttpServletRequest request
) {
    String subdomain = extractSubdomain(request);
    examService.startNormalExam(examId, subdomain);
    return ResponseEntity.ok(
        examService.startNormalExam(examId, subdomain));
}




@GetMapping("/questions/{qid}")
public ResponseEntity<Map<String, Object>> getQuestionByQid(
        @PathVariable String qid,
        HttpServletRequest request) {

    String host = request.getServerName(); // brihathi.localhost
    String subdomain = host.split("\\.")[0];

    return ResponseEntity.ok(
            examService.getQuestionByQid(qid, subdomain)
    );
}


@GetMapping("/{userId}/generated-qids/{examId}")
public ResponseEntity<Map<String, Object>> getGeneratedQids(
    @PathVariable Long examId,
    HttpServletRequest request
         ) {
            String host = request.getServerName(); // brihathi.localhost
            String subdomain = host.split("\\.")[0];
    Map<String, Object> response =
            examService.getGeneratedQids(examId, subdomain);

    return ResponseEntity.ok(response);
}

@PostMapping("/{examId}/abort")
public ResponseEntity<AbortExamResponseDTO> abortNormalExam(
        @PathVariable Long examId,
        HttpServletRequest request
) {
    String host = request.getServerName(); // brihathi.localhost
    String subdomain = host.split("\\.")[0];

    return ResponseEntity.ok(
            examService.abortExam(examId, subdomain)
    );
}




@PostMapping("/{examId}/update")
public ResponseEntity<Map<String, Object>> updateQuestion(
        @PathVariable Long examId,
        @RequestBody UpdateQuestionRequestDTO request,
        HttpServletRequest httpRequest
) {
    String subdomain = httpRequest.getServerName().split("\\.")[0];

    QuestionStatusDTO status =
            examService.updateQuestion(examId, request, subdomain);

    return ResponseEntity.ok(
            Map.of(
                "questionStatus", status,
                "message", "Question status updated successfully"
            )
    );
}


@PostMapping("/{examId}/end")
public ResponseEntity<EndExamSummaryDTO> endExam(
        @PathVariable Long examId,
        HttpServletRequest request
) {
    String subdomain = request.getServerName().split("\\.")[0];

    return ResponseEntity.ok(
            examService.endNormalExam(examId, subdomain)
    );
}



@PostMapping("/{examId}/confirm-submission")
public ResponseEntity<Map<String, Object>> confirmSubmission(
        @PathVariable Long examId,
        HttpServletRequest request
) {
    String subdomain = request.getServerName().split("\\.")[0];

    return ResponseEntity.ok(
            examService.confirmSubmission(examId, subdomain)
    );
}


@GetMapping("/final-results/{examId}")
public ResponseEntity<Map<String, Object>> getFinalResults(
        @PathVariable Long examId,
        HttpServletRequest request
) {

    String subdomain = request.getServerName().split("\\.")[0];
    return ResponseEntity.ok(
            examService.getFinalResults(examId, subdomain)
    );
}


}
 
 
 
 