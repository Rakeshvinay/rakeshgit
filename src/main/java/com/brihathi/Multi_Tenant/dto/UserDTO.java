package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDTO {

    private Long userId;
    private String name;
    private Long tenantId;
    private LocalDateTime updatedAt;
   
}


// package com.brihathi.Multi_Tenant.dto;
 
// import lombok.*;
 
 
 
// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class UserDTO {
 
//     private Long userId;
//     private String enrollmentId;
//     private String name;
//     //private String email;
//     private String phoneNumber;
//     private String grade;
//     private String state;
//     private String city;
// }
