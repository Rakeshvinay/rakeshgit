package com.brihathi.Multi_Tenant.entity;

    
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
 
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    private Tenant tenant;

   @Column(name = "tenant_id")
    private Long tenantId;

 
    @Column(nullable = false, length = 32)
    @jakarta.validation.constraints.Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters")
    @jakarta.validation.constraints.NotBlank(message = "Name is required")
    private String name;
    @Column(nullable = false, unique = true)
    private String enrollmentId;
    private String password;
    private Long phoneNumber;
    private LocalDate dateOfBirth;
    private String grade;
    private String state;
    private String city;
    
 
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
 
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 
    @Column(name = "last_login")
    @JsonProperty("lastLogin")
    private LocalDateTime lastLogin;

    @Column(name = "current_token", length = 512)
    private String currentToken;
 
    public String getCurrentToken() {
        return currentToken;
    }
 
    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }
 
    
    @Column(name = "branch")
    private String branch;
 
    @Column(name = "batch", length = 100)
    private String batch;

 
 
    //Store image as BLOB
    @Column(name = "profile_image", length = 255)
    private String profileImage;
    @Column(name = "parent_name", length = 255)
    private String parentName;

    @Column(name = "parent_phone", length = 20)
    private long parentPhone;

    
    // --- UserDetails overrides ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
 
    @Override
    public String getPassword() {
        return password;
    }
 
    @Override
    public String getUsername() {
        return enrollmentId;
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return true;
    }
 
 
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
 
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
 
    







 

 
}
 
 
 
