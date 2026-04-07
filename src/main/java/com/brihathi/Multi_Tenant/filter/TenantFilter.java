
// package com.brihathi.Multi_Tenant.filter;

// import com.brihathi.Multi_Tenant.context.TenantContext;
// import com.brihathi.Multi_Tenant.entity.Tenant;
// import com.brihathi.Multi_Tenant.repository.TenantRepository;
// import jakarta.servlet.*;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.io.IOException;

// @Component
// public class TenantFilter implements Filter {

//     private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

//     private final TenantRepository tenantRepository;
//     private final TenantContext tenantContext;

//     @Value("${app.domain}")
//     private String mainDomain; // localhost / brightswan.ai

//     @Value("${app.default-tenant}")
// private String defaultTenant;


//     public TenantFilter(TenantRepository tenantRepository, TenantContext tenantContext) {
//         this.tenantRepository = tenantRepository;
//         this.tenantContext = tenantContext;
//     }

//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//             throws IOException, ServletException {

//         HttpServletRequest http = (HttpServletRequest) request;
//         HttpServletResponse httpResp = (HttpServletResponse) response;

//         String host = http.getHeader("Host"); // better than getServerName()
//         if (host == null) host = http.getServerName();

//         logger.info("Incoming Request - Host: {}, Path: {}", host, http.getRequestURI());

//         // remove port → vikas.localhost:3000 → vikas.localhost
//         String cleanHost = host.split(":")[0];

//         String subdomain = extractSubdomain(cleanHost);

//         logger.info("Extracted Tenant Subdomain = {}", subdomain);

//         // Look up tenant
//         Tenant tenant = tenantRepository.findBySubdomain(subdomain).orElse(null);

//         if (tenant == null) {
//             logger.warn("❌ Tenant '{}' not found in DB", subdomain);
//             httpResp.setStatus(HttpServletResponse.SC_FORBIDDEN);
//             httpResp.getWriter().write("Forbidden: Invalid tenant '" + subdomain + "'");
//             return;
//         }

//         tenantContext.setTenant(subdomain);
//         logger.info("TenantContext SET → {}", subdomain);

//         try {
//             chain.doFilter(request, response);
//         } finally {
//             tenantContext.clear();
//             logger.info("TenantContext CLEARED");
//         }
//     }

//     /**
//      * Extract subdomain from host.
//      * Examples:
//      * - vikas.localhost       → vikas
//      * - brihathi.localhost    → brihathi
//      * - modulus.brightswan.ai → modulus
//      * - brightswan.ai         → default
//      */
//     private String extractSubdomain(String host) {

//         // localhost cases
//         if (host.equals("localhost") || host.startsWith("localhost")) {
//             return "default"; // no tenant, base environment
//         }

//         // For local multi-tenant: (vikas.localhost)
//         if (host.endsWith("localhost")) {
//             return host.replace(".localhost", "");
//         }

//         // For production: (vikas.brightswan.ai)
//         if (host.endsWith(mainDomain)) {
//             String sub = host.replace("." + mainDomain, "");

//             if (sub.equals(mainDomain)) return "default";  // brightswan.ai (no tenant)
//             return sub;
//         }

//         // Unknown / impossible host
//         return "default";
//     }

//     // private String extractSubdomain(String host) {

//     //     // 1️⃣ localhost (NO subdomain)
//     //     if (host.equals("localhost")) {
//     //         return defaultTenant; // 👉 brihathi
//     //     }
    
//     //     // 2️⃣ tenant.localhost (brihathi.localhost)
//     //     if (host.endsWith(".localhost")) {
//     //         return host.replace(".localhost", "");
//     //     }
    
//     //     // 3️⃣ tenant.production-domain (vikas.brightswan.ai)
//     //     if (host.endsWith("." + mainDomain)) {
//     //         return host.replace("." + mainDomain, "");
//     //     }
    
//     //     // 4️⃣ production root domain (brightswan.ai)
//     //     if (host.equals(mainDomain)) {
//     //         return defaultTenant;
//     //     }
    
//     //     // 5️⃣ fallback safety
//     //     return defaultTenant;
//     // }
    
// }

package com.brihathi.Multi_Tenant.filter;
 
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
import com.brihathi.Multi_Tenant.context.TenantContext;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import java.io.IOException;
 
@Component
public class TenantFilter implements Filter {
 
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
 
    private final TenantRepository tenantRepository;
    private final TenantContext tenantContext;
 
    @Value("${app.domain}")
    private String mainDomain; // localhost / jeeswan.in
 
    public TenantFilter(TenantRepository tenantRepository, TenantContext tenantContext) {
        this.tenantRepository = tenantRepository;
        this.tenantContext = tenantContext;
    }
 
    // @Override
    // public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    //         throws IOException, ServletException {
 
    //     HttpServletRequest http = (HttpServletRequest) request;
    //     HttpServletResponse httpResp = (HttpServletResponse) response;
 
    //     String path = http.getRequestURI();
 
    //     // ✅ 1. SKIP TENANT LOGIC FOR STATIC IMAGES
    //     // This prevents the "NoResourceFoundException" by allowing Spring's
    //     // ResourceHandler to pick up the request before the filter interferes.
    //     if (path.startsWith("/images/")) {
    //         logger.info("Static resource request detected: {}. Skipping TenantFilter.", path);
    //         chain.doFilter(request, response);
    //         return;
    //     }
 
    //     String host = http.getHeader("Host");
    //     if (host == null) host = http.getServerName();
 
    //     logger.info("Incoming Request - Host: {}, Path: {}", host, path);
 
    //     // remove port → vikas.localhost:3000 → vikas.localhost
    //     String cleanHost = host.split(":")[0];
    //     String subdomain = extractsubdomain(cleanHost);
 
    //     logger.info("Extracted Tenant subdomain = {}", subdomain);
 
    //     // Look up tenant
    //     Tenant tenant = tenantRepository.findBySubdomain(subdomain).orElse(null);
 
    //     if (tenant == null) {
    //         logger.warn("❌ Tenant '{}' not found in DB", subdomain);
    //         httpResp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    //         httpResp.getWriter().write("Forbidden: Invalid tenant '" + subdomain + "'");
    //         return;
    //     }
 
    //     tenantContext.setTenant(subdomain);
    //     logger.info("TenantContext SET → {}", subdomain);
 
    //     try {
    //         chain.doFilter(request, response);
    //     } finally {
    //         tenantContext.clear();
    //         logger.info("TenantContext CLEARED");
    //     }
    // }
 
 
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
   
        HttpServletRequest http = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;
   
        String path = http.getRequestURI();
   
        // ✅ Allow WebSocket handshake without tenant validation
        if (path.startsWith("/ws")) {
            logger.info("WebSocket request detected → Skipping TenantFilter");
            chain.doFilter(request, response);
            return;
        }
   
       
   
        String host = http.getHeader("Host");
        if (host == null) host = http.getServerName();
   
        logger.info("Incoming Request - Host: {}, Path: {}", host, path);
   
        // remove port → vikas.localhost:3000 → vikas.localhost
        String cleanHost = host.split(":")[0];
   
        String subdomain = extractsubdomain(cleanHost);
   
        logger.info("Extracted Tenant subdomain = {}", subdomain);
   
        // Look up tenant
        Tenant tenant = tenantRepository.findBySubdomain(subdomain).orElse(null);
   
        if (tenant == null) {
            logger.warn("❌ Tenant '{}' not found in DB", subdomain);
            httpResp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResp.getWriter().write("Forbidden: Invalid tenant '" + subdomain + "'");
            return;
        }
   
        tenantContext.setTenant(subdomain);
        logger.info("TenantContext SET → {}", subdomain);
   
        try {
            chain.doFilter(request, response);
        } finally {
            tenantContext.clear();
            logger.info("TenantContext CLEARED");
        }
    }
    private String extractsubdomain(String host) {
        // Handle base domain cases
        if (host.equals("jeeswan.in") || host.equals("localhost")) {
            return "default";
        }
 
        // For production: brihathi.jeeswan.in
        if (host.endsWith(".jeeswan.in")) {
            return host.replace(".jeeswan.in", "");
        }
 
        // For local dev: brihathi.localhost
        if (host.endsWith(".localhost")) {
            return host.replace(".localhost", "");
        }
 
        // Fallback for main domain matches
        if (host.endsWith(mainDomain)) {
            String sub = host.replace("." + mainDomain, "");
            if (sub.equals(mainDomain)) return "default";
            return sub;
        }
 
        return "default";
    }
}
 
 