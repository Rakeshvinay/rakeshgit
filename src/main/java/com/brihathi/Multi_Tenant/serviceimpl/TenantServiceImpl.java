package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.context.TenantContext;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.TenantStaff;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.TenantStaffRepository;
import com.brihathi.Multi_Tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantStaffRepository tenantStaffRepository;

    @Autowired
    private TenantContext tenantContext;

    @Override
    public List<TenantStaff> getTenantStaffByTenantId(Long tenantId) {
        return tenantStaffRepository.findByTenantId(tenantId);
    }

    @Override
    public Tenant getTenantFromContext() {

        String subdomain = tenantContext.getTenant();  

        return tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new RuntimeException("Invalid tenant: " + subdomain));
    
    }

    @Override
public Tenant getTenantBySubdomain(String subdomain) {
    return tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + subdomain));
}

}
