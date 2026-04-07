package com.brihathi.Multi_Tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.brihathi.Multi_Tenant.entity.TenantStaff;
import java.util.Optional;
import java.util.List;   ;



public interface TenantStaffRepository extends JpaRepository<TenantStaff, Long> {
    List<TenantStaff> findByTenantId(Long tenantId);
}
