package com.brihathi.Multi_Tenant.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.brihathi.Multi_Tenant.entity.Tenant;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySubdomain(String subdomain);
    Optional<Tenant> findById(Long id);
   // Optional<Tenant> findBySubDomain(String subdomain);

    boolean existsBySubdomain(String subdomain);

    
}
