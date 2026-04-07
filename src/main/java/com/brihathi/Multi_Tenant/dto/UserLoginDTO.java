package com.brihathi.Multi_Tenant.dto;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    private String loginIdentifier;
    private String password;
 
}
