package com.brihathi.Multi_Tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.brihathi.Multi_Tenant.entity.Branch;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BranchRepository extends JpaRepository<Branch, Long> {

    // get all branches for a tenant
    List<Branch> findByTenantId(Long tenantId);

    @Query("""
        SELECT b.branchId, b.branchName,
               COUNT(DISTINCT bt.batchId),
               COUNT(DISTINCT u.userId)
        FROM Branch b
        LEFT JOIN Batch bt ON bt.branchId = b.branchId
        LEFT JOIN User u ON u.branch = b.branchName AND u.tenantId = b.tenantId
        WHERE b.tenantId = :tenantId
        GROUP BY b.branchId, b.branchName
        """)
        List<Object[]> getBranchStats(@Param("tenantId") Long tenantId);
        

}

