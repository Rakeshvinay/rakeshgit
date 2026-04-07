 
package com.brihathi.Multi_Tenant.controller;
 
import com.brihathi.Multi_Tenant.dto.EducatorUploadResponseDTO;
import com.brihathi.Multi_Tenant.dto.EmailPasswordResetRequest;
import com.brihathi.Multi_Tenant.dto.PasswordResetResultResponse;
import com.brihathi.Multi_Tenant.dto.ResetPasswordRequest;
import com.brihathi.Multi_Tenant.dto.PasswordVerificationRequest;
import com.brihathi.Multi_Tenant.dto.PasswordVerificationResponse;
import com.brihathi.Multi_Tenant.dto.ResetEmailResponse;
import com.brihathi.Multi_Tenant.dto.SendResetEmailRequest;
import com.brihathi.Multi_Tenant.dto.EducatorResponse;
 
import com.brihathi.Multi_Tenant.dto.LoginResponse;
import com.brihathi.Multi_Tenant.entity.Educator;
 
import com.brihathi.Multi_Tenant.dto.LoginUserDto;
import com.brihathi.Multi_Tenant.dto.PhoneCheckResponse;
 
import com.brihathi.Multi_Tenant.service.EducatorService;
 
import org.springframework.http.ResponseEntity;
import java.util.Map;
 
import org.springframework.web.bind.annotation.*;
 
import org.springframework.web.multipart.MultipartFile;
 
@RestController
 
@RequestMapping("/api/educators")
 
public class EducatorController {
 
    private final EducatorService educatorService;
 
    public EducatorController(EducatorService educatorService) {
 
        this.educatorService = educatorService;
 
    }
 
    @PostMapping("/upload")
 
    public ResponseEntity<EducatorUploadResponseDTO> uploadEducators(
 
            @RequestParam("file") MultipartFile file) {
 
        return ResponseEntity.ok(educatorService.uploadEducators(file));
 
    }
 
    @PostMapping("/login")
 
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto dto) {
 
        return ResponseEntity.ok(educatorService.login(dto));
 
    }
 
    @PostMapping("/reset-password-email")
    public ResponseEntity<PasswordResetResultResponse> resetPasswordEmail(@RequestBody EmailPasswordResetRequest request) {
        if (request.getEmail() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(new PasswordResetResultResponse("Missing required fields: email and newPassword", null));
        }
        Map<String, Object> result = educatorService.handlePasswordReset(request.getEmail(), request.getNewPassword());
        PasswordResetResultResponse response = new PasswordResetResultResponse(
            (String) result.getOrDefault("message", ""),
            (Educator) result.getOrDefault("educator", null)
        );
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/check-phone/{phoneNumber}")
    public ResponseEntity<PhoneCheckResponse> checkPhoneNumberExists(@PathVariable String phoneNumber) {
        boolean exists = educatorService.isPhoneNumberExists(Long.parseLong(phoneNumber));
        PhoneCheckResponse response = new PhoneCheckResponse(
            exists,
            exists ? "Phone number exists" : "Phone number not found"
        );
        return ResponseEntity.ok(response);
    }
 
    @PostMapping("/reset-phone-password")
    public ResponseEntity<PasswordResetResultResponse> resetPhonePassword(@RequestBody ResetPasswordRequest request) {
        if (request.getPhoneNumber() == null || request.getOtp() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(new PasswordResetResultResponse("Missing required fields: phoneNumber, otp, and newPassword", null));
        }
        Map<String, Object> result = educatorService.handlePhonePasswordReset(request.getPhoneNumber(), request.getOtp(), request.getNewPassword());
        PasswordResetResultResponse response = new PasswordResetResultResponse(
            (String) result.getOrDefault("message", ""),
            (Educator) result.getOrDefault("educator", null)
        );
        return ResponseEntity.ok(response);
    }
 
 
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody PasswordVerificationRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields: email and password"));
        }
        boolean isValid = educatorService.verifyPassword(request.getEmail(), request.getPassword());
        PasswordVerificationResponse response = new PasswordVerificationResponse(
            isValid,
            isValid ? "Password is valid" : "Invalid password"
        );
        return ResponseEntity.ok(response);
    }
 
 
    @PostMapping("/send-reset-email")
    public ResponseEntity<ResetEmailResponse> sendResetEmail(@RequestBody SendResetEmailRequest request) {
        if (request.getInput() == null || request.getInput().isEmpty()) {
            return ResponseEntity.badRequest().body(new ResetEmailResponse("Input (email or phone number) is required", null, null, null, null, null, null, null));
        }
        Map<String, String> requestMap = Map.of("input", request.getInput());
        Map<String, Object> result = educatorService.sendResetEmail(requestMap);
        // Map to DTO
        ResetEmailResponse response = new ResetEmailResponse(
            (String) result.getOrDefault("message", ""),
            (String) result.getOrDefault("otp", null),
            (String) result.getOrDefault("token", null),
            result.get("expiresIn") != null ? Long.valueOf(result.get("expiresIn").toString()) : null,
            result.get("userId") != null ? Long.valueOf(result.get("userId").toString()) : null,
            (String) result.getOrDefault("name", null),
            (String) result.getOrDefault("email", null),
            result.get("phoneNumber") != null ? result.get("phoneNumber").toString() : null
        );
        return ResponseEntity.ok(response);
    }
 


     
    @GetMapping("/educator-details")
    public ResponseEntity<EducatorResponse> authenticatedUser() {
        return ResponseEntity.ok(new EducatorResponse(educatorService.getAuthenticatedEducator()));
    }
 
    @PutMapping("/educator-update")
    public ResponseEntity<EducatorResponse> updateUserProfile(@RequestBody Educator updatedEducator) {
        Educator savedEducator = educatorService.updateEducatorProfile(updatedEducator);
        return ResponseEntity.ok(new EducatorResponse(savedEducator));
    }
 
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResultResponse> resetPassword(@RequestBody EmailPasswordResetRequest request) {
        if (request.getEmail() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(new PasswordResetResultResponse("Missing required fields: email and newPassword", null));
        }
        Map<String, Object> result = educatorService.handlePasswordReset(request.getEmail(), request.getNewPassword());
        PasswordResetResultResponse response = new PasswordResetResultResponse(
            (String) result.getOrDefault("message", ""),
            (Educator) result.getOrDefault("educator", null)
        );
        return ResponseEntity.ok(response);
    }
 
}
 
 
 