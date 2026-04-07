
package com.brihathi.Multi_Tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import com.brihathi.Multi_Tenant.filter.JwtAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         //http.cors(cors -> cors.disable())
         http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/ws").permitAll()
                .requestMatchers("/",
                    "/images/**",
                    "/api/tenant/**",
                    "/api/tenant-details",
                    "/api/users/uploading-users",
                    "/api/users/login",
                    "/api/users/creategmailuser",
                    "/api/{tenantId}/plans",
                    "/api/coupon/available",
                    "/api/coupon/apply",
                    "/api/payment/success",
                    "/api/payment/failure",
                    "/api/educators/upload",
                    "/api/educators/login",
		    "/api/admins",
		    "/api/chapters/upload",
		    "/api/questions/upload",
		    "/api/educators/check-phone/**",
		    "/api/educators/generate-token/**",
		    "/api/educators/send-reset-email",
                    "/api/educators/reset-phone-password",
		    "/api/educators/reset-password-email",
            "/api/educators/verify-email",
		    "/api/images/**",
                    "/api/users/google-signin",
                    "/api/auth/google","/api/otp/send",
                    "/api/otp/verify"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration config = new CorsConfiguration();
        
    //     // Allow trusted frontend domains only
    //    config.setAllowedOriginPatterns(List.of(
    //         "http://localhost:3000",
    //         "http://localhost:8080",
    //         "http://localhost",
    //         "http://157.180.86.201",
    //         "http://157.180.86.201:8080",
    //         "http://brihathi.net",
    //         "http://brihathi.net:8080",
    //         "https://brihathi.net",
    //         "https://brihathi.net:8080",
    //         "http://brihathi.localhost:3000",
    //         "http://brihathi.localhost:8080",
    //         "http://localhost:3000",
    //         "http://localhost:8080",
    //         "http://localhost",

    //         "http://brihathi.localhost",
    //         "http://brihathi.157.180.86.201",
    //         "http://brihathi.157.180.86.201:8080",
    //         "http://brihathi.brihathi.net",
    //         "http://brihathi.brihathi.net:8080",
    //         "https://brihathi.brihathi.net",
    //         "https://brihathi.brihathi.net:8080"
    //     ));
    //      config.setAllowedOriginPatterns(List.of("*"));
    //     config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //     config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
    //     config.setExposedHeaders(List.of("Authorization"));
    //     config.setAllowCredentials(true);  // Only use if you're sending cookies or Authorization headers
    //     config.setMaxAge(3600L);

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return source;
    // }

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // ⭐ Allow all subdomains automatically
    config.setAllowedOriginPatterns(List.of(
        "http://*.localhost",
        "http://*.localhost:*",
        "http://*.neetswan.in",
        "https://*.neetswan.in",
        "http://neetswan.in",
        "https://neetswan.in",
        "http://localhost:[*]",
        "*"
    ));

    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Tenant", "Accept"));
    config.setExposedHeaders(List.of("Authorization"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}

}

