package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.EducatorUploadResponseDTO;
import com.brihathi.Multi_Tenant.dto.LoginResponse;
import com.brihathi.Multi_Tenant.dto.LoginUserDto;
import com.brihathi.Multi_Tenant.entity.Educator;
import com.brihathi.Multi_Tenant.entity.User;
 
import java.util.Map;
 
import org.springframework.web.multipart.MultipartFile;
 
public interface EducatorService {
 
    EducatorUploadResponseDTO uploadEducators(MultipartFile file);
    Map<String, Object> handlePasswordReset(String email, String newPassword);
    Educator getEducatorByEmail(String email);
    LoginResponse login(LoginUserDto loginUserDto);
    boolean isPhoneNumberExists(Long phoneNumber);
    Educator getEducatorByPhoneNumber(Long phoneNumber);
    boolean verifyPassword(String email, String password);
    Map<String, Object> handlePhonePasswordReset(String phoneNumber, String otp, String newPassword);
    Map<String, Object> sendResetEmail(Map<String, String> request);
    //Educator getEducatorByEmail(String email);
    Educator getAuthenticatedEducator();
 
    Educator updateEducatorProfile(Educator updatedEducator);
}
 
 
 