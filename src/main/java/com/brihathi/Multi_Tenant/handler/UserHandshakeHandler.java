package com.brihathi.Multi_Tenant.handler;
 
import java.security.Principal;
import java.util.Map;
 
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
 
import com.brihathi.Multi_Tenant.principal.UserPrincipal;
 
public class UserHandshakeHandler extends DefaultHandshakeHandler {
 
//     @Override
//     protected Principal determineUser(ServerHttpRequest request,
//                                       WebSocketHandler wsHandler,
//                                       Map<String, Object> attributes) {
 
//         // Long userId = (Long) attributes.get("userId");
 
//         // return new UserPrincipal(userId);
//         Long id = (Long) attributes.get("id");
// String role = (String) attributes.get("role");

// return new UserPrincipal(role + "_" + id);
//     }
@Override
protected Principal determineUser(ServerHttpRequest request,
                                  WebSocketHandler wsHandler,
                                  Map<String, Object> attributes) {

    return (Principal) attributes.get("principal"); // ✅ use interceptor value
}
}
 
 