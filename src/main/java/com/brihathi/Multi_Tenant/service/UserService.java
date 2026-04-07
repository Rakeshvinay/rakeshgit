package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.entity.User;

import com.brihathi.Multi_Tenant.dto.LoginUserDto;
import com.brihathi.Multi_Tenant.dto.UserUploadResponseDTO;
import com.brihathi.Multi_Tenant.dto.LoginResponse;
import com.brihathi.Multi_Tenant.dto.UserLoginDTO;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;   

public interface UserService {
 
    LoginResponse login(UserLoginDTO userLoginDTO);
    User getAuthenticatedUser();


    User getUserByPhoneNumber(String phoneNumber);
    User getUserByPhoneNumber(Long phoneNumber);
    boolean isPhoneNumberExists(Long phoneNumber);

    String generateEmailVerificationToken(String email);
    User updateUserProfile(User updatedUser);
    void updateProfileImage(Long id, String filename);
    UserUploadResponseDTO uploadUsers(MultipartFile file);
   
    
    String generateJwtForUser(User user);
    Map<String, Object> handlePasswordReset(String enrollmentId, String newPassword);
boolean verifyPassword(String enrollmentId, String password);
 
 
    

}

 
