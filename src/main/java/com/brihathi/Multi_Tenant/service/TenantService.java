package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.entity.TenantStaff;


import com.brihathi.Multi_Tenant.entity.Tenant;
import java.util.List;

public interface TenantService {

    Tenant getTenantFromContext();
    Tenant getTenantBySubdomain(String subdomain);

    List<TenantStaff> getTenantStaffByTenantId(Long tenantId);


}
