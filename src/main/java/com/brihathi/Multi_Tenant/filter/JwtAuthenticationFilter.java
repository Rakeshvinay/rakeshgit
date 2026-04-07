 
package com.brihathi.Multi_Tenant.filter;
 
import com.brihathi.Multi_Tenant.service.JwtService;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.EducatorRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Educator;
import com.brihathi.Multi_Tenant.entity.Tenant;
 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.beans.factory.annotation.Qualifier;
 
import java.io.IOException;
import java.util.Optional;
 
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EducatorRepository educatorRepository;
    private final TenantRepository tenantRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
 
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            EducatorRepository educatorRepository,
            TenantRepository tenantRepository,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.educatorRepository = educatorRepository;
        this.tenantRepository = tenantRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }
 
//     @Override
//     protected void doFilterInternal(
//             @NonNull HttpServletRequest request,
//             @NonNull HttpServletResponse response,
//             @NonNull FilterChain filterChain
//     ) throws ServletException, IOException {
 
//         try {
//             final String path = request.getRequestURI();
 
//             // ---------- PUBLIC APIs ----------
//             if (path.startsWith("/api/users/login")
//                     || path.startsWith("/api/educators/login")
//                     || path.startsWith("/api/otp")
//                     || path.startsWith("/auth")
//                     || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
 
//                 filterChain.doFilter(request, response);
//                 return;
//             }
 
//             final String authHeader = request.getHeader("Authorization");
 
//             if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                 filterChain.doFilter(request, response);
//                 return;
//             }
 
//             final String jwt = authHeader.substring(7);
//             final String username = jwtService.extractUsername(jwt);
//             final String userType = jwtService.extractClaim(jwt, c -> c.get("userType", String.class));
//             final Long tokenTenantId = jwtService.extractClaim(jwt, c -> c.get("tenantId", Long.class));
 
//             if (username == null || userType == null || tokenTenantId == null) {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 response.getWriter().write("Invalid token");
//                 return;
//             }
 
//             Object principal;
 
//             if ("USER".equalsIgnoreCase(userType)) {
//                 User user = userRepository.findByEnrollmentId(username)
//                         .orElseThrow(() -> new RuntimeException("User not found"));
 
//                 if (user.getCurrentToken() == null || !jwt.equals(user.getCurrentToken())) {
//                     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                     response.getWriter().write("Invalid session");
//                     return;
//                 }
//                 principal = user;
 
//             } else if ("EDUCATOR".equalsIgnoreCase(userType)) {
//                 Educator educator = educatorRepository.findByEmail(username)
//                         .orElseThrow(() -> new RuntimeException("Educator not found"));
 
//                 if (educator.getCurrentToken() == null || !jwt.equals(educator.getCurrentToken())) {
//                     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                     response.getWriter().write("Invalid session");
//                     return;
//                 }
//                 principal = educator;
 
//             } else {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 response.getWriter().write("Invalid user type");
//                 return;
//             }
 
//             // ---------- TENANT VALIDATION ----------
//             String host = request.getHeader("Host");
//             if (host == null || host.isBlank()) {
//                 host = request.getServerName();
//             }
 
//             // String subdomain = extractSubdomain(host);
 
//             // Tenant requestTenant = tenantRepository.findBySubdomain(subdomain)
//             //         .orElseThrow(() -> new RuntimeException("Invalid tenant"));
//             String subdomain = extractSubdomain(host);
 
// /* 🟢 Allow internal system calls (RabbitMQ, scheduler, etc.) */
// if (subdomain == null || subdomain.isBlank()) {
//     filterChain.doFilter(request, response);
//     return;
// }
 
// Optional<Tenant> tenantOpt = tenantRepository.findBySubdomain(subdomain);
 
// if (tenantOpt.isEmpty()) {
//     filterChain.doFilter(request, response);
//     return;
// }
 
// Tenant requestTenant = tenantOpt.get();
 
 
//             if (!requestTenant.getTenantId().equals(tokenTenantId)) {
//                 response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                 response.getWriter().write("Tenant mismatch");
//                 return;
//             }
 
//             UsernamePasswordAuthenticationToken authToken =
//                     new UsernamePasswordAuthenticationToken(
//                             principal,
//                             null,
//                             principal instanceof User
//                                     ? ((User) principal).getAuthorities()
//                                     : ((Educator) principal).getAuthorities()
//                     );
 
//             authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//             SecurityContextHolder.getContext().setAuthentication(authToken);
 
//             filterChain.doFilter(request, response);
 
//         } catch (Exception ex) {
//             handlerExceptionResolver.resolveException(request, response, null, ex);
//         }
//     }
@Override
protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
) throws ServletException, IOException {
 
    try {
        final String path = request.getRequestURI();
 
        // ✅ Allow WebSocket without JWT
if (path.startsWith("/ws")
    || path.startsWith("/topic")
    || path.startsWith("/queue")) {
 
filterChain.doFilter(request, response);
return;
}
 
        // ---------- PUBLIC APIs ----------
        if (path.startsWith("/api/users/login")
                || path.startsWith("/api/educators/login")
                || path.startsWith("/api/otp")
                || path.startsWith("/auth")
                || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
 
            filterChain.doFilter(request, response);
            return;
        }
 
        final String authHeader = request.getHeader("Authorization");
 
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
 
        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);
        final String userType = jwtService.extractClaim(jwt, c -> c.get("userType", String.class));
        final Long tokenTenantId = jwtService.extractClaim(jwt, c -> c.get("tenantId", Long.class));
 
        if (username == null || userType == null || tokenTenantId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }
 
        Object principal;
 
        if ("USER".equalsIgnoreCase(userType)) {
            User user = userRepository.findByEnrollmentId(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
 
            if (user.getCurrentToken() == null || !jwt.equals(user.getCurrentToken())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid session");
                return;
            }
            principal = user;
 
        } else if ("EDUCATOR".equalsIgnoreCase(userType)) {
            Educator educator = educatorRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Educator not found"));
 
            if (educator.getCurrentToken() == null || !jwt.equals(educator.getCurrentToken())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid session");
                return;
            }
            principal = educator;
 
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid user type");
            return;
        }
 
        // ---------- TENANT VALIDATION ----------
        String host = request.getHeader("Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }
 
        // String subdomain = extractSubdomain(host);
 
        // Tenant requestTenant = tenantRepository.findBySubdomain(subdomain)
        //         .orElseThrow(() -> new RuntimeException("Invalid tenant"));
        String subdomain = extractSubdomain(host);
 
/* 🟢 Allow internal system calls (RabbitMQ, scheduler, etc.) */
if (subdomain == null || subdomain.isBlank()) {
filterChain.doFilter(request, response);
return;
}
 
Optional<Tenant> tenantOpt = tenantRepository.findBySubdomain(subdomain);
 
if (tenantOpt.isEmpty()) {
filterChain.doFilter(request, response);
return;
}
 
Tenant requestTenant = tenantOpt.get();
 
 
        if (!requestTenant.getTenantId().equals(tokenTenantId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Tenant mismatch");
            return;
        }
 
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal instanceof User
                                ? ((User) principal).getAuthorities()
                                : ((Educator) principal).getAuthorities()
                );
 
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
 
        filterChain.doFilter(request, response);
 
    } catch (Exception ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}
 
    // ---------- SUBDOMAIN EXTRACTOR ----------
    private String extractSubdomain(String host) {
        String h = host.split(":")[0];
        String[] parts = h.split("\\.");
        return parts.length > 1 ? parts[0] : h;
    }
}
 
 