package com.brihathi.Multi_Tenant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {
    private String loginIdentifier; // Accepts email or phone
    private String password;

    // Utility methods to determine login type
    public boolean isEmail() {
        return loginIdentifier != null && loginIdentifier.contains("@");
    }

    public boolean isPhone() {
        return loginIdentifier != null && !loginIdentifier.contains("@");
    }
}

