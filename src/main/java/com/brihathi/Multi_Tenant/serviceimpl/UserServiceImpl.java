package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.context.TenantContext;
import com.brihathi.Multi_Tenant.dto.LoginResponse;
import com.brihathi.Multi_Tenant.dto.LoginUserDto;
import com.brihathi.Multi_Tenant.dto.UserLoginDTO;
import com.brihathi.Multi_Tenant.dto.UserExcelDTO;
import com.brihathi.Multi_Tenant.dto.UserUploadResponseDTO;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.service.UserService;


import jakarta.persistence.EntityNotFoundException;

import com.brihathi.Multi_Tenant.service.EmailService;
import com.brihathi.Multi_Tenant.service.JwtService;
import com.brihathi.Multi_Tenant.service.OtpService;
import com.brihathi.Multi_Tenant.helper.ExcelHelper;

//import com.brihathi.Multi_Tenant.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Transactional
@Service
public class UserServiceImpl implements UserService {
     private final UserRepository userRepository;
     private final TenantRepository tenantRepository;
     private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final TenantContext tenantContext;
     private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
 

    // @Autowired
    // public UserValidator userValidator;
 
    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

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

    public UserServiceImpl(
            UserRepository userRepository,
            TenantRepository tenantRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            TenantContext tenantContext,
            // JwtService jwtService,
            //UserValidator userValidator,
            OtpService otpService,
            EmailService emailService //@Value("${web_url}") String weburl
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantContext = tenantContext;
        // this.jwtService = jwtService;
        //this.userValidator = userValidator;
        this.otpService = otpService;
        //this.weburl = weburl;
        this.emailService = emailService;
    }
 
    @Override
    public String generateEmailVerificationToken(String email) {
        // No longer set on User entity, use in-memory map
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plus(24, ChronoUnit.HOURS);
        verificationTokens.put(email, new TokenInfo(token, expiry));
        return token;
    }


    @Override
    public String generateJwtForUser(User user) {
        String token = jwtService.generateToken(user);
        user.setCurrentToken(token);
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);
        return token;
    }

    @Override
    public boolean isPhoneNumberExists(Long phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
 
    @Override
    public boolean verifyPassword(String enrollmentId, String password) {
        try {
            User user = userRepository.findByEnrollmentId(enrollmentId).orElseThrow(() -> new EntityNotFoundException("User not found with enrollmentId: " + enrollmentId));
            if (user != null && user.getPassword() != null) {
                return passwordEncoder.matches(password, user.getPassword());
            }
            return false;
        } catch (Exception e) {
            // logger.error("Error verifying password for email: {}", email, e);
            return false;
        }
    }
     
    @Override
    public Map<String, Object> handlePasswordReset(String enrollmentId, String newPassword) {
        if (enrollmentId == null || newPassword == null) {
            throw new RuntimeException("Missing required fields");
        }
        User updatedUser = userRepository.findByEnrollmentId(enrollmentId).orElseThrow(() -> new EntityNotFoundException("User not found with enrollmentId: " + enrollmentId));
        updatedUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(updatedUser);
        return Map.of(
            "message", "Password reset successfully",
            "user", updatedUser
        );
    }
     
     

    @Transactional
    public User resetPhonePassword(String phoneNumber, String otp, String newPassword) {
        try {
            Long phoneNumberLong = Long.parseLong(phoneNumber);
            User user = getUserByPhoneNumber(phoneNumberLong);
            // TODO: Verify OTP
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid phone number format");
        }
    }


    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        try {
            Long phoneNumberLong = Long.parseLong(phoneNumber);
            return getUserByPhoneNumber(phoneNumberLong);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid phone number format");
        }
    }

    @Override
    public User getUserByPhoneNumber(Long phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + phoneNumber));
    }



    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return (User) authentication.getPrincipal();
    }

    @Override
    @Transactional
    public User updateUserProfile(User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        currentUser.setName(updatedUser.getName());
        currentUser.setPhoneNumber(updatedUser.getPhoneNumber());
        currentUser.setDateOfBirth(updatedUser.getDateOfBirth());
        currentUser.setGrade(updatedUser.getGrade());
        currentUser.setState(updatedUser.getState());
        currentUser.setCity(updatedUser.getCity());
        return userRepository.save(currentUser);
    }



 
    @Override
    public void updateProfileImage(Long userId, String filename) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        user.setProfileImage(filename);
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(UserLoginDTO userLoginDTO) {
     
        // 1️⃣ Extract tenant from subdomain (already set by filter)
        String subdomain = tenantContext.getTenant();
        if (subdomain == null) {
            throw new RuntimeException("Tenant not found in request");
        }
     
        // 2️⃣ Get tenant entity
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new RuntimeException("Invalid tenant"));
     
        // 3️⃣ Normalize login identifier
        try {
            // Long phoneNumber = Long.parseLong(userLoginDTO.getLoginIdentifier());
            // User userByPhone = userRepository
            //         .findByPhoneNumberAndTenant_TenantId(phoneNumber, tenant.getTenantId())
            //         .orElseThrow(() -> new RuntimeException("Invalid phone number"));
     
            userLoginDTO.setLoginIdentifier(userLoginDTO.getLoginIdentifier());
        } catch (NumberFormatException e) {
            userLoginDTO.setLoginIdentifier(userLoginDTO.getLoginIdentifier().toLowerCase());
        }
     
        // 4️⃣ Fetch user ONLY from this tenant
        User existingUser = userRepository
                .findByEnrollmentIdAndTenant_TenantId(
                        userLoginDTO.getLoginIdentifier(),
                        tenant.getTenantId()
                )
                .orElseThrow(() ->
                        new RuntimeException("You are not registered under this institute")
                );
     
        String batch = existingUser.getBatch();
     
        // 5️⃣ Authenticate (Spring Security)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getLoginIdentifier(),
                        userLoginDTO.getPassword()
                )
        );
     
        SecurityContextHolder.getContext().setAuthentication(authentication);
     
        User user = (User) authentication.getPrincipal();
     
        // 6️⃣ Update login metadata
        user.setBatch(batch);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
     
        // 7️⃣ Generate JWT
        String token = jwtService.generateToken(user);
        user.setCurrentToken(token);
        userRepository.save(user);
     
        // 8️⃣ Build response
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setBatch(batch);
        response.setExpiresIn(jwtService.getExpirationTime());
        response.setLastLogin(user.getLastLogin());
     
        return response;
    }
     
    



    @Override
    public UserUploadResponseDTO uploadUsers(MultipartFile file) {

        if (!ExcelHelper.isExcelFormat(file)) {
            return new UserUploadResponseDTO(0, 0, 0, "Upload only .xlsx format");
        }

        try {
            List<UserExcelDTO> list = ExcelHelper.excelToUserList(file.getInputStream());
            int success = 0, failed = 0;

            for (UserExcelDTO dto : list) {

                if (dto.getEnrollmentId() == null || userRepository.existsByEnrollmentId(dto.getEnrollmentId())) {
                    failed++;
                    continue;
                }

                User user = new User();

                user.setName(dto.getName());
                user.setEnrollmentId(dto.getEnrollmentId());
                //user.setPassword(dto.getPassword());
                String rawPassword = dto.getPassword();
String encodedPassword = passwordEncoder.encode(rawPassword);
System.out.println("Raw password: " + rawPassword);
System.out.println("Encoded password: " + encodedPassword);

user.setPassword(encodedPassword);

                user.setPhoneNumber(dto.getPhoneNumber());
               //user.setPassword(passwordEncoder.encode(user.getPassword()));

                if (dto.getDateOfBirth() != null)
                    user.setDateOfBirth(dto.getDateOfBirth());

                user.setGrade(dto.getGrade());
                user.setState(dto.getState());
                user.setCity(dto.getCity());
                user.setBranch(dto.getBranch());
                if (dto.getBatch() != null) {
                    user.setBatch(dto.getBatch());
            }

                user.setProfileImage(dto.getProfileImage());

                if (dto.getLastLogin() != null)
                    user.setLastLogin(LocalDateTime.parse(dto.getLastLogin(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                user.setCurrentToken(dto.getCurrentToken());
                user.setParentName(dto.getParentName());
                user.setParentPhone(dto.getParentPhone());
                user.setTenantId(dto.getTenantId());

                userRepository.save(user);
                success++;
            }

            return new UserUploadResponseDTO(list.size(), success, failed,
                    "Upload completed. Success: " + success + ", Failed: " + failed);

        } catch (Exception e) {
            return new UserUploadResponseDTO(0, 0, 0, "Error: " + e.getMessage());
        }
    }
 
}
