package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentSearchDTO {

    private Long userId;
    private String name;
    private String enrollmentId;
}
