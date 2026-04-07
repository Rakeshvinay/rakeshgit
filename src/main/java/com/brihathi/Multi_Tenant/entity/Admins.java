package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admins {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminId;
 
    @Column(name = "name", nullable = false, length = 100)
    private String name;
 
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
 
    @Column(name = "password", nullable = false, length = 255)
    private String password;
 
    @Column(name = "role", length = 50)
    private String role;
 
    @Column(name = "status", length = 20)
    private String status;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
 