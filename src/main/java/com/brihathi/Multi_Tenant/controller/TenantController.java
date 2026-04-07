

package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.TenantStaff;
import com.brihathi.Multi_Tenant.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TenantController {

    @Autowired
    private TenantService tenantService;

    // @GetMapping("/api/tenant")
     @GetMapping("/")
    public ResponseEntity<?> getTenantInfo(HttpServletRequest request) {

        // Get the Host header (e.g., vikas.neetswan.ai or vikas.localhost:8081)
        String host = request.getHeader("host");
        if (host == null || host.isEmpty()) {
            return ResponseEntity.badRequest().body("Host header missing");
        }

        // Extract the subdomain (tenant name) from the host
        // This assumes the format is <tenant>.<domain>, e.g. vikas.neetswan.ai
        String subdomain = host.split("\\.")[0];

        // Fetch tenant info by tenant name
       // Tenant tenant = tenantService.getTenantFromContext();
       Tenant tenant = tenantService.getTenantBySubdomain(subdomain);
        if (tenant == null) {
            return ResponseEntity.status(404).body("Tenant not found");
        }
       List<TenantStaff> tenantstaff = tenantService.getTenantStaffByTenantId(tenant.getTenantId());


        // Prepare response map
        Map<String, Object> response = new HashMap<>();
        response.put("tenantId", tenant.getTenantId());
        response.put("subdomain", tenant.getSubdomain());
        response.put("tenantLogo", tenant.getTenantLogo());
        response.put("theme", tenant.getTheme());
        response.put("background" , tenant.getBackGround());
        response.put("textColour", tenant.getTextColour());
        response.put("tenantEmail", tenant.getTenantEmail());
        response.put("password" , tenant.getPassword());
        response.put("collegeName", tenant.getCollegeName());
        response.put("heroImage", tenant.getHeroImage());
        response.put("tenantPhone", tenant.getTenantPhone());
        response.put("tenantAddress", tenant.getTenantAddress());
        response.put("tenantStaff", tenantstaff);

 
 

        return ResponseEntity.ok(response);
    }
}
