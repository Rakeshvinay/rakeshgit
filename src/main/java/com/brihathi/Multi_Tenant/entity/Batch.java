package com.brihathi.Multi_Tenant.entity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "batches")
public class Batch {
   
    @Id
    @Column(name = "batch_id")
    private String  batchId;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "branch_id")
    private String branchId;
 
    @Column(name = "batch_name")
    private String batchName;
   
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
   
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
   
}
 
 