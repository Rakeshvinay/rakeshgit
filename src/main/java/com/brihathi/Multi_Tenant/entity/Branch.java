package com.brihathi.Multi_Tenant.entity;
 
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "branches")
public class Branch {
   
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private String branchId;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "branch_name")
    private String branchName;
   
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
 
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
   
}
 
 