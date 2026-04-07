// package com.brihathi.Multi_Tenant.serviceimpl;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.brihathi.Multi_Tenant.dto.GoogleUserDto;
// import com.brihathi.Multi_Tenant.entity.Tenant;
// import com.brihathi.Multi_Tenant.entity.User;
// // import com.brihathi.Multi_Tenant.enums.Course;
// import com.brihathi.Multi_Tenant.repository.UserRepository;
// import com.brihathi.Multi_Tenant.repository.TenantRepository;
// import com.brihathi.Multi_Tenant.service.JwtService;
// import com.brihathi.Multi_Tenant.service.GoogleAuthService;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.util.UriComponentsBuilder;
// import org.springframework.beans.factory.annotation.Autowired;
// import java.io.IOException;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.util.Optional;
 
// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class GoogleAuthServiceImpl implements GoogleAuthService {
    
//     @Autowired
//     private UserServiceImpl userServiceImpl;

//     @Value("${google.client-id}")
//     private String clientId;
 
//     @Value("${google.client-secret}")
//     private String clientSecret;
 
//     @Value("${google.redirect-uri}")
//     private String redirectUri;
 
//     private final UserRepository userRepository;
//     private final TenantRepository tenantRepository;
//     private final JwtService jwtService;
//     private final ObjectMapper objectMapper;
 
//     @Override
//     public String getGoogleAuthorizationUrl() {
//         return UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
//                 .queryParam("client_id", clientId)
//                 .queryParam("redirect_uri", redirectUri)
//                 .queryParam("response_type", "code")
//                 .queryParam("scope", "openid email profile")
//                 .queryParam("access_type", "offline")
//                 .build().toUriString();
//     }
 
//     @Override
//     public GoogleUserDto processOAuth2Callback(String code) {
//         try {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create("https://oauth2.googleapis.com/token"))
//                     .header("Content-Type", "application/x-www-form-urlencoded")
//                     .POST(HttpRequest.BodyPublishers.ofString("code=" + code
//                             + "&client_id=" + clientId
//                             + "&client_secret=" + clientSecret
//                             + "&redirect_uri=" + redirectUri
//                             + "&grant_type=authorization_code"))
//                     .build();
 
//             HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//             JsonNode jsonNode = objectMapper.readTree(response.body());
//             String accessToken = jsonNode.get("access_token").asText();
 
//             HttpRequest userInfoRequest = HttpRequest.newBuilder()
//                     .uri(URI.create("https://www.googleapis.com/oauth2/v3/userinfo"))
//                     .header("Authorization", "Bearer " + accessToken)
//                     .build();
 
//             HttpResponse<String> userInfoResponse = HttpClient.newHttpClient().send(userInfoRequest, HttpResponse.BodyHandlers.ofString());
//             JsonNode userJson = objectMapper.readTree(userInfoResponse.body());
 
//             return new GoogleUserDto(
//                     userJson.get("email").asText(),
//                     userJson.get("name").asText()
//             );
//         } catch (IOException | InterruptedException e) {
//             throw new RuntimeException("Failed to fetch user info from Google", e);
//         }
//     }
 
//     @Override
//     public String loginOrRegisterUser(GoogleUserDto userDto) {
//         Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
 
//         User user = optionalUser.orElseGet(() -> {
//             User newUser = new User();
//             newUser.setEmail(userDto.getEmail());
//             newUser.setName(userDto.getName());
//             newUser.setSource(Course.GMAIL);
//             Long tenantIdFromFE = newUser.getTenantId();
//             if (tenantIdFromFE == null) {
//                 throw new RuntimeException("tenantId is required in request body.");
//             }

//             // 🔥 2. Load tenant from DB
//             Tenant tenant = tenantRepository.findById(tenantIdFromFE)
//                 .orElseThrow(() -> new RuntimeException("Invalid tenantId: " + tenantIdFromFE));

   
//             newUser.setTenant(tenant);
//             newUser.setEmailVerified(true);
//             newUser.setUserType(UserType.STUDENT);
//             newUser.setReferralCode(userServiceImpl.generateUniqueReferralCode());
//             return userRepository.save(newUser);
//         });
 
//         return jwtService.generateToken(user);
//     }
// }

