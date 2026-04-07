
package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Column(name = "subdomain", length = 255)
    private String subdomain;
 
    @Column(name = "tenant_logo", length = 300, nullable = false, unique = true)
    private String tenantLogo;

    @Column(name = "hero_image")
    private String heroImage;

    @Column(name = "college_name")
    private String collegeName;
 
    @Column(name = "tenant_email", nullable = false, unique = true)
    private String tenantEmail;
 
    @Column(name = "password")
    private String password;
 
    @Column(name = "tenant_phone")
    private Long tenantPhone;
 
    @Column(name = "tenant_address")
    private String tenantAddress;
 
    @Column(name = "theme", length = 100)
    private String theme;
 
    @Column(name = "background")
    private String backGround;
 
    @Column(name = "text_colour")
    private String textColour;
 
    @Column(name = "question_table", length = 100)
    private String questionTable;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
 
 
