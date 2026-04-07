package com.brihathi.Multi_Tenant.dto;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private long expiresIn;
    
    private LocalDateTime lastLogin;

    private String batch;

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    

    // public String getCourse() {
    //     return course;
    // }

    // public void setCourse(String course) {
    //     this.course = course;
    // }
}
