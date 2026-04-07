package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NormalExamResponse {
    private NormalExamDTO exam;
    private UserDTO user;
}
