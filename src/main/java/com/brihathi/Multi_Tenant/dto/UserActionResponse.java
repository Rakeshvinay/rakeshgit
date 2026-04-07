package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;   


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActionResponse {
    private String message;
    private User user;
}
