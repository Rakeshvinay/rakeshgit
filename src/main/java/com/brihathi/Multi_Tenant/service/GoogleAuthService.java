package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.GoogleUserDto;
import org.springframework.stereotype.Service;
 
public interface GoogleAuthService {
    String getGoogleAuthorizationUrl();
 
    GoogleUserDto processOAuth2Callback(String code);
 
    String loginOrRegisterUser(GoogleUserDto userDto);
}
