

package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.service.QuestionUploadService;
import com.brihathi.Multi_Tenant.service.AdminService;
import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.dto.ExamInfoDTO;
import com.brihathi.Multi_Tenant.dto.QuestionUploadSuccessResponse;
import com.brihathi.Multi_Tenant.dto.QuestionCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QuestionUploadController {

    private final QuestionUploadService uploadService;
    private final AdminService adminService;

    /**
     * Upload Excel file.
     * Admin credentials are NOT required here — tenant validation is done using the tenant_id in the Excel
     * and comparing the tenant name to the request subdomain.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadQuestions(
            @RequestPart(value = "file", required = true) MultipartFile file,
            HttpServletRequest request) throws Exception {

        log.info("Received file upload request: {}", file != null ? file.getOriginalFilename() : "null");

        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is required"));
            }

            // Extract request subdomain (e.g. brihathi from brihathi.localhost:8080)
            String host = request.getHeader("Host");
            if (host == null || host.isBlank()) {
                host = request.getServerName();
            }
            String subdomain = extractSubdomain(host);
            log.debug("Detected subdomain from request host '{}': '{}'", host, subdomain);

            // Delegate to service which will validate tenant_id in Excel against this subdomain
            uploadService.importExcelFile(file, subdomain);

            log.info("File processed successfully: {}", file.getOriginalFilename());
            return ResponseEntity.ok(new QuestionUploadSuccessResponse(
                    "Questions uploaded successfully",
                    file.getOriginalFilename()
            ));
        } catch (IllegalArgumentException iae) {
            log.warn("Validation error uploading questions: {}", iae.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", iae.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading questions: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload questions"));
        }
    }

   

//     @GetMapping("/getexaminfo")
// public ResponseEntity<?> getExamInfo(HttpServletRequest request) {

//     String host = request.getHeader("Host");
//     if (host == null || host.isBlank()) {
//         host = request.getServerName();
//     }

//     String subdomain = extractSubdomain(host);

//     ExamInfoDTO examInfo = uploadService.getExamInfo(subdomain);
//     return ResponseEntity.ok(examInfo);
// }

    /**
     * Simple subdomain extractor:
     * - Removes port if present
     * - Returns first segment before the first dot
     * Examples:
     *  - brihathi.localhost -> brihathi
     *  - tenant.example.com -> tenant
     *  - localhost -> localhost
     */
    private static String extractSubdomain(String host) {
        if (host == null) return "";
        String h = host.split(":")[0];
        String[] parts = h.split("\\.");
        if (parts.length == 0) return "";
        if (parts.length == 1) return parts[0];
        return parts[0];
    }
}
