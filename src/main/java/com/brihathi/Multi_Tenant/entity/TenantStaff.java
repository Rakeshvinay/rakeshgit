package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
 
import java.time.LocalDateTime;
@Entity
@Table(name = "tenant_staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantStaff {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mid")
    private Long mId;
 
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "name")
    private String name;

    @Column(name = "profile_image")
    private String profileImage;
 
    @Column(name = "role")
    private String role;
 
    @Column(name = "department")
    private String department;
 
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
 
 
   
}