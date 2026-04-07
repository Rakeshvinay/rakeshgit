 
package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.context.TenantContext;
 
import com.brihathi.Multi_Tenant.dto.EducatorExcelDTO;
 
import com.brihathi.Multi_Tenant.dto.EducatorUploadResponseDTO;
 
import com.brihathi.Multi_Tenant.dto.LoginResponse;
 
import com.brihathi.Multi_Tenant.dto.LoginUserDto;
 
import com.brihathi.Multi_Tenant.entity.Educator;
 
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.helper.ExcelHelper;
 
import com.brihathi.Multi_Tenant.repository.EducatorRepository;
 
import com.brihathi.Multi_Tenant.repository.TenantRepository;
 
import com.brihathi.Multi_Tenant.service.EducatorService;
 
import com.brihathi.Multi_Tenant.service.JwtService;
import com.brihathi.Multi_Tenant.service.OtpService;
import com.brihathi.Multi_Tenant.service.EmailService;
 
import jakarta.persistence.EntityNotFoundException;
 
import org.springframework.beans.factory.annotation.Autowired;
 
import org.springframework.security.crypto.password.PasswordEncoder;
 
import org.springframework.stereotype.Service;
 
import org.springframework.transaction.annotation.Transactional;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
 
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
 
@Service
 
@Transactional
 
public class EducatorServiceImpl implements EducatorService {
 
    @Autowired
 
    private EducatorRepository educatorRepository;
 
    @Autowired
 
    private TenantRepository tenantRepository;
 
    @Autowired
 
    private PasswordEncoder passwordEncoder;
 
    @Autowired
 
    private JwtService jwtService;
 
    @Autowired
    private EmailService emailService;
 
    @Autowired
    private OtpService otpService;
 
    @Autowired
 
    private TenantContext tenantContext;
 
 
    private final Map<String, TokenInfo> verificationTokens = new ConcurrentHashMap<>();
    private final Map<String, TokenInfo> passwordResetTokens = new ConcurrentHashMap<>();
 
     private static class TokenInfo {
        String token;
        LocalDateTime expiry;
        TokenInfo(String token, LocalDateTime expiry) {
            this.token = token;
            this.expiry = expiry;
        }
    }
 
    // =========================================================
 
    // EXCEL UPLOAD (ALL OR NOTHING, tenant_id FROM EXCEL)
 
    // =========================================================
 
    @Override
 
    public EducatorUploadResponseDTO uploadEducators(MultipartFile file) {
 
        if (!ExcelHelper.isExcelFormat(file)) {
 
            return new EducatorUploadResponseDTO(0, 0, 0, "Upload only .xlsx format");
 
        }
 
        try {
 
            List<EducatorExcelDTO> list =
 
                    ExcelHelper.excelToEducatorList(file.getInputStream());
 
            // ===============================
 
            // 1️⃣ VALIDATION PHASE
 
            // ===============================
 
            for (EducatorExcelDTO dto : list) {
 
                if (dto.getTenantId() == null) {
 
                    return new EducatorUploadResponseDTO(
 
                            list.size(), 0, list.size(),
 
                            "Upload failed: tenant_id missing in Excel"
 
                    );
 
                }
 
                if (dto.getEmail() == null) {
 
                    return new EducatorUploadResponseDTO(
 
                            list.size(), 0, list.size(),
 
                            "Upload failed: email missing in Excel"
 
                    );
 
                }
 
                if (educatorRepository.existsByEmailAndTenantId(
 
                        dto.getEmail(),
 
                        dto.getTenantId()
 
                )) {
 
                    return new EducatorUploadResponseDTO(
 
                            list.size(), 0, list.size(),
 
                            "Upload failed: duplicate email for tenant_id " + dto.getTenantId()
 
                    );
 
                }
 
            }
 
            // ===============================
 
            // 2️⃣ SAVE PHASE
 
            // ===============================
 
            for (EducatorExcelDTO dto : list) {
 
                Educator educator = new Educator();
 
                educator.setEducatorName(dto.getEducatorName());
 
                educator.setEmail(dto.getEmail());
 
                educator.setPassword(passwordEncoder.encode(dto.getPassword()));
 
                educator.setPhoneNumber(dto.getPhoneNumber());
 
                educator.setSubject(dto.getSubject());
 
                educator.setTenantId(dto.getTenantId()); // 🔥 FROM EXCEL
 
                educator.setStatus("ACTIVE");
 
                educator.setCreatedAt(LocalDateTime.now());
 
                // educator.setUpdatedAt(LocalDateTime.now());
 
                educatorRepository.save(educator);
 
            }
 
            return new EducatorUploadResponseDTO(
 
                    list.size(), list.size(), 0,
 
                    "Upload successful. All records saved."
 
            );
 
        } catch (Exception e) {
 
            return new EducatorUploadResponseDTO(
 
                    0, 0, 0,
 
                    "Error: " + e.getMessage()
 
            );
 
        }
 
    }
 
    // =========================================================
 
    // LOGIN (TENANT SAFE + JWT FOR EDUCATOR)
 
    // =========================================================
 
    @Override
 
    public LoginResponse login(LoginUserDto loginUserDto) {
 
        // 1️⃣ Resolve tenant from subdomain
 
        String subdomain = tenantContext.getTenant();
 
        if (subdomain == null) {
 
            throw new RuntimeException("Tenant not found in request");
 
        }
 
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
 
                .orElseThrow(() -> new RuntimeException("Invalid tenant"));
 
        // 2️⃣ Normalize identifier
 
        try {
 
            Long phone = Long.parseLong(loginUserDto.getLoginIdentifier());
 
            Educator byPhone =
 
                    educatorRepository
 
                            .findByPhoneNumberAndTenantId(phone, tenant.getTenantId())
 
                            .orElseThrow(() -> new RuntimeException("Invalid phone number"));
 
            loginUserDto.setLoginIdentifier(byPhone.getEmail());
 
        } catch (NumberFormatException e) {
 
            loginUserDto.setLoginIdentifier(
 
                    loginUserDto.getLoginIdentifier().toLowerCase()
 
            );
 
        }
 
        // 3️⃣ Fetch educator (tenant-bound)
 
        Educator educator =
 
                educatorRepository
 
                        .findByEmailAndTenantId(
 
                                loginUserDto.getLoginIdentifier(),
 
                                tenant.getTenantId()
 
                        )
 
                        .orElseThrow(() ->
 
                                new RuntimeException("You are not registered under this institute")
 
                        );
 
        // 4️⃣ Password check
 
        if (!passwordEncoder.matches(
 
                loginUserDto.getPassword(),
 
                educator.getPassword()
 
        )) {
 
            throw new RuntimeException("Invalid credentials");
 
        }
 
        // 5️⃣ Update login metadata
 
        educator.setLastLogin(LocalDateTime.now());
 
        // 6️⃣ Generate JWT (EDUCATOR-SPECIFIC)
 
        String token = jwtService.generateToken(educator);
 
        educator.setCurrentToken(token);
 
        educatorRepository.save(educator);
 
        // 7️⃣ Response
 
        LoginResponse response = new LoginResponse();
        
        response.setToken(token);
 
        response.setExpiresIn(jwtService.getExpirationTime());
 
        response.setLastLogin(educator.getLastLogin());
 
        return response;
 
    }
 
    @Override
    public Map<String, Object> handlePasswordReset(String email, String newPassword) {
        if (email == null || newPassword == null) {
            throw new RuntimeException("Missing required fields");
        }
        Educator updatedEducator = resetPassword(email, newPassword);
        return Map.of(
            "message", "Password reset successfully",
            "user", updatedEducator
        );
    }
 
    @Transactional
    public Educator resetPassword(String email, String newPassword) {
        Educator educator = getEducatorByEmail(email);
        educator.setPassword(passwordEncoder.encode(newPassword));
        return educatorRepository.save(educator);
    }
 
 
    @Override
    public Educator getEducatorByEmail(String email) {
        return educatorRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Educator not found with email: " + email));
    }
 
    @Override
    public boolean isPhoneNumberExists(Long phoneNumber) {
        return educatorRepository.existsByPhoneNumber(phoneNumber);
    }
 
    @Override
    public Map<String, Object> handlePhonePasswordReset(String phoneNumber, String otp, String newPassword) {
        if (phoneNumber == null || otp == null || newPassword == null) {
            throw new RuntimeException("Missing required fields");
        }
        Educator updatedEducator = resetPhonePassword(phoneNumber, otp, newPassword);
        return Map.of(
            "message", "Password reset successfully",
            "educator", updatedEducator
        );
    }
 
     @Transactional
    public Educator resetPhonePassword(String phoneNumber, String otp, String newPassword) {
        try {
            Long phoneNumberLong = Long.parseLong(phoneNumber);
            Educator educator = getEducatorByPhoneNumber(phoneNumberLong);
            // TODO: Verify OTP
            educator.setPassword(passwordEncoder.encode(newPassword));
            return educatorRepository.save(educator);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid phone number format");
        }
    }
 
    @Override
    public Educator getEducatorByPhoneNumber(Long phoneNumber) {
        return educatorRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + phoneNumber));
    }
 
    @Override
    public boolean verifyPassword(String email, String password) {
        try {
            Educator educator = getEducatorByEmail(email);
            if (educator != null && educator.getPassword() != null) {
                return passwordEncoder.matches(password, educator.getPassword());
            }
            return false;
        } catch (Exception e) {
            //logger.error("Error verifying password for email: {}", email, e);
            return false;
        }
    }
 
 
     @Override
    public Map<String, Object> sendResetEmail(Map<String, String> request) {
        String input = request.get("input");
 
        if (input == null || input.isEmpty()) {
            throw new RuntimeException("Input (email or phone number) is required");
        }
 
        boolean isEmail = input.contains("@");
        Educator existingEducator;
 
        try {
            if (isEmail) {
                existingEducator = getEducatorByEmail(input);
                if (existingEducator == null) {
                    throw new RuntimeException("No account found with this email address. Please check your email or sign up.");
                }
 
            //  FETCH TENANT FROM USER
            Long tenantId = existingEducator.getTenantId();
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));
 
            String subdomain = tenant.getSubdomain();  
               
                // Generate password reset token
                String resetToken = generatePasswordResetToken(input);
               // String resetLink = "http://localhost:8080/reset-password?email=" + input + "&token=" + resetToken;
               // Build dynamic URL
            String resetLink = "http://" + subdomain + ".localhost:8080/reset-password-email?email="
            + input + "&token=" + resetToken;
 
 
                emailService.sendPasswordResetEmail(input, resetLink);
                return Map.of("message", "Reset link sent successfully to your email");
            } else {
                existingEducator = getEducatorByPhoneNumber(Long.parseLong(input));
                if (existingEducator == null) {
                    throw new RuntimeException("No account found with this phone number. Please check your number or sign up.");
                }
                otpService.sendOtp(input);
                {
                    String jwtToken = jwtService.generateToken(existingEducator);
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "OTP sent successfully to your phone number");
                    // Do not include OTP in response
                    response.put("token", jwtToken);
                    response.put("expiresIn", jwtService.getExpirationTime());
                    response.put("userId", existingEducator.getEducatorId());
                    response.put("name", existingEducator.getEducatorName());
                    response.put("email", existingEducator.getEmail());
                    response.put("phoneNumber", existingEducator.getPhoneNumber());
                    return response;
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid input format");
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
 
     
    public String generatePasswordResetToken(String email) {
        // No longer set on User entity, use in-memory map
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plus(1, ChronoUnit.HOURS); // 1 hour expiry
        passwordResetTokens.put(email, new TokenInfo(token, expiry));
        return token;
    }
 
   
    public Educator getUserByPhoneNumber(Long phoneNumber) {
        try {
            return educatorRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Educator not found with phone number: " + phoneNumber));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid phone number format");
        }
    }
 
   
    // public Educator getEducatorByEmail(String email) {
    //     return educatorRepository.findByEmail(email)
    //             .orElseThrow(() -> new EntityNotFoundException("Educator not found with email: " + email));
    // }
 
 
    @Override
    public Educator getAuthenticatedEducator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return (Educator) authentication.getPrincipal();
    }
 
 
    @Override
    @Transactional
    public Educator updateEducatorProfile(Educator updatedEducator) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Educator currentEducator = (Educator) authentication.getPrincipal();
        currentEducator.setEducatorName(updatedEducator.getEducatorName());
        currentEducator.setPhoneNumber(updatedEducator.getPhoneNumber());
        return educatorRepository.save(currentEducator);
    }
 
 
}
 
 
 