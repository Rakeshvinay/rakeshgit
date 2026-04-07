package com.brihathi.Multi_Tenant.config;
 
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
 
import com.brihathi.Multi_Tenant.handler.UserHandshakeHandler;
import com.brihathi.Multi_Tenant.interceptor.JwtHandshakeInterceptor;
 
import lombok.RequiredArgsConstructor;
 
 
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
 
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new UserHandshakeHandler()) // ⭐ important
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
 
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
 
        registry.enableSimpleBroker("/topic", "/queue");
 
        registry.setApplicationDestinationPrefixes("/app");
 
        registry.setUserDestinationPrefix("/user");
    }
}
 
// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// import org.springframework.web.socket.config.annotation.*;
 
// import com.brihathi.multitenant.interceptor.JwtHandshakeInterceptor;
 
// import lombok.RequiredArgsConstructor;
 
// @Configuration
// @EnableWebSocketMessageBroker
// @RequiredArgsConstructor
// public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 
//     private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
 
//     @Override
//     public void registerStompEndpoints(StompEndpointRegistry registry) {
 
//         registry.addEndpoint("/ws")
//                 .addInterceptors(jwtHandshakeInterceptor)
//                 .setAllowedOriginPatterns("*");
//     }
 
//     @Override
//     public void configureMessageBroker(MessageBrokerRegistry registry) {
 
//         registry.enableSimpleBroker("/topic", "/queue");
 
//         registry.setApplicationDestinationPrefixes("/app");
 
//         registry.setUserDestinationPrefix("/user");
//     }
// }
 