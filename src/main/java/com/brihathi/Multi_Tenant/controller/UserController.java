package com.brihathi.Multi_Tenant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.brihathi.Multi_Tenant.dto.EmailVerificationResponse;
import com.brihathi.Multi_Tenant.dto.EmailPasswordResetRequest;
import com.brihathi.Multi_Tenant.dto.PasswordResetResultResponse;
import com.brihathi.Multi_Tenant.dto.PasswordVerificationRequest;
import com.brihathi.Multi_Tenant.dto.PasswordVerificationResponse;
import com.brihathi.Multi_Tenant.dto.PhoneCheckResponse;
import com.brihathi.Multi_Tenant.dto.UserUploadResponseDTO;
import com.brihathi.Multi_Tenant.dto.UserExcelDTO;
import com.brihathi.Multi_Tenant.dto.ResetEmailResponse;
import com.brihathi.Multi_Tenant.dto.SendResetEmailRequest;
import com.brihathi.Multi_Tenant.dto.ResetPasswordRequest;
import com.brihathi.Multi_Tenant.dto.EmailVerificationResultResponse;
import com.brihathi.Multi_Tenant.dto.LoginUserDto;
import com.brihathi.Multi_Tenant.dto.UserLoginDTO;
import com.brihathi.Multi_Tenant.dto.LoginResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.dto.UserActionResponse;
import com.brihathi.Multi_Tenant.dto.PasswordResetResultStudentResponse;
import com.brihathi.Multi_Tenant.dto.EnrollmentPasswordResetRequest;
import com.brihathi.Multi_Tenant.dto.PasswordVerificationStudentRequest;
import com.brihathi.Multi_Tenant.dto.UserResponse;
import com.brihathi.Multi_Tenant.service.EmailService;
import com.brihathi.Multi_Tenant.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.nio.file.Paths;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

@Transactional
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public UserController(UserService userService, ModelMapper modelMapper, EmailService emailService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }
    

    @GetMapping("/me")
    public ResponseEntity<UserResponse> authenticatedUser() {
        return ResponseEntity.ok(new UserResponse(userService.getAuthenticatedUser()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUserProfile(@RequestBody User updatedUser) {
        User savedUser = userService.updateUserProfile(updatedUser);
        return ResponseEntity.ok(new UserResponse(savedUser));
    }



    @PostMapping("/uploading-users")
    public ResponseEntity<UserUploadResponseDTO> uploadUsers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadUsers(file));
    }

    @GetMapping("/check-phone/{phoneNumber}")
    public ResponseEntity<PhoneCheckResponse> checkPhoneNumberExists(@PathVariable String phoneNumber) {
        boolean exists = userService.isPhoneNumberExists(Long.parseLong(phoneNumber));
        PhoneCheckResponse response = new PhoneCheckResponse(
            exists,
            exists ? "Phone number exists" : "Phone number not found"
        );
        return ResponseEntity.ok(response);
    }

    

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String uploadDir = "C:/opt/swan/profile-pictures/";
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.toLowerCase().endsWith(".jpg") && !originalFilename.toLowerCase().endsWith(".jpeg") && !originalFilename.toLowerCase().endsWith(".png"))) {
            return ResponseEntity.badRequest().body("Only JPG,JPEG and PNG files are allowed,and the File size  must be less than 1MB.");
        }
	if (file.getSize() > 1 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(" Only JPG,JPEG and PNG files are allowed,and the File size  must be less than 1MB. ");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String filename = java.util.UUID.randomUUID() + extension;
        try {
            Path path = Paths.get(uploadDir + filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());
            userService.updateProfileImage(id, filename);
            return ResponseEntity.ok("Image uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed.");
        }
    }

     @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(userService.login(userLoginDTO));
    }
   
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResultStudentResponse> resetPassword(@RequestBody EnrollmentPasswordResetRequest request) {
        if (request.getEnrollmentId() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(new PasswordResetResultStudentResponse("Missing required fields: enrollmentId and newPassword", null));
        }
        Map<String, Object> result = userService.handlePasswordReset(request.getEnrollmentId(), request.getNewPassword());
        PasswordResetResultStudentResponse response = new PasswordResetResultStudentResponse(
            (String) result.getOrDefault("message", ""),
            (User) result.getOrDefault("user", null)
        );
        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody PasswordVerificationStudentRequest request) {
        if (request.getEnrollmentId() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields: email and password"));
        }
        boolean isValid = userService.verifyPassword(request.getEnrollmentId(), request.getPassword());
        PasswordVerificationResponse response = new PasswordVerificationResponse(
            isValid,
            isValid ? "Password is valid" : "Invalid password"
        );
        return ResponseEntity.ok(response);
    }
     
   
}