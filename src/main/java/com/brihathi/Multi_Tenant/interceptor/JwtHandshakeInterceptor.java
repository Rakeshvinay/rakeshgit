// package com.brihathi.multitenant.interceptor;
 
 
 
// import java.util.Map;
 
// import org.springframework.http.server.*;
// import org.springframework.stereotype.Component;
// import org.springframework.web.socket.*;
// import org.springframework.web.socket.server.HandshakeInterceptor;
 
// import com.brihathi.multitenant.service.JwtService;
 
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
 
// @Component
// @RequiredArgsConstructor
// public class JwtHandshakeInterceptor implements HandshakeInterceptor {
 
//     private final JwtService jwtService;
 
//     @Override
//     public boolean beforeHandshake(ServerHttpRequest request,
//                                    ServerHttpResponse response,
//                                    WebSocketHandler wsHandler,
//                                    Map<String, Object> attributes) {
 
//         if (request instanceof ServletServerHttpRequest servletRequest) {
 
//             HttpServletRequest req = servletRequest.getServletRequest();
 
//             String token = req.getParameter("token");
 
//             if (token == null) {
//                 return false;
//             }
 
//             try {
 
//                 Long userId = jwtService.extractUserId(token);
 
//                 attributes.put("userId", userId);
 
//             } catch (Exception e) {
//                 return false;
//             }
//         }
 
//         return true;
//     }
 
//     @Override
//     public void afterHandshake(ServerHttpRequest request,
//                                ServerHttpResponse response,
//                                WebSocketHandler wsHandler,
//                                Exception exception) {
//     }
// }
 
 
package com.brihathi.Multi_Tenant.interceptor;
 
import java.util.Map;
 
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
 
import com.brihathi.Multi_Tenant.principal.UserPrincipal;
import com.brihathi.Multi_Tenant.service.JwtService;
 
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
 
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
 
    private final JwtService jwtService;
 
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
 
        if (request instanceof ServletServerHttpRequest servletRequest) {
 
            HttpServletRequest req = servletRequest.getServletRequest();
 
            // Get token from query param
            String token = req.getParameter("token");
 
            if (token == null || token.isEmpty()) {
                System.out.println("❌ WebSocket connection rejected: token missing");
                return false;
            }
 
            try {
 
                // // Extract userId from JWT
                // Long userId = jwtService.extractUserId(token);
 
                // // Store in attributes
                // attributes.put("userId", userId);
 
                // // IMPORTANT: attach principal for /user messaging
                // attributes.put("principal", new UserPrincipal(userId));

                String userType = jwtService.extractUserType(token);

                if ("USER".equalsIgnoreCase(userType)) {
                
                    Long userId = jwtService.extractUserId(token);
                
                    String principalName = "USER_" + userId;
                
                    attributes.put("id", userId);
                    attributes.put("role", "USER");
                    attributes.put("principal", new UserPrincipal(principalName)); // ✅ ADD THIS
                
                } else if ("EDUCATOR".equalsIgnoreCase(userType)) {
                
                    Long educatorId = jwtService.extractEducatorId(token);
                
                    String principalName = "EDUCATOR_" + educatorId;
                
                    attributes.put("id", educatorId);
                    attributes.put("role", "EDUCATOR");
                    attributes.put("principal", new UserPrincipal(principalName)); // ✅ ADD THIS
                
                } else {
                    return false;
                }
 
                System.out.println("✅ WebSocket authenticated for userId: " + userType);
 
            } catch (Exception e) {
 
                System.out.println("❌ Invalid JWT token");
                return false;
            }
        }
 
        return true;
    }
 
    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
 
        System.out.println("🔌 WebSocket handshake completed");
    }
}
 