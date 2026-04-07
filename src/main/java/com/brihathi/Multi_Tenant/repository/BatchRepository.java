package com.brihathi.Multi_Tenant.repository;


import com.brihathi.Multi_Tenant.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    // get all batches for a branch
    List<Batch> findByBranchId(String branchId);


    @Query("""
SELECT bt.batchId, bt.batchName, COUNT(u.userId)
FROM Batch bt
LEFT JOIN User u ON u.batch = bt.batchName AND u.tenantId = bt.tenantId
WHERE bt.branchId = :branchId
GROUP BY bt.batchId, bt.batchName
""")
List<Object[]> getBatchStats(@Param("branchId") String branchId);



}

