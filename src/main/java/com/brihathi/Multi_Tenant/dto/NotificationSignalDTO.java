package com.brihathi.Multi_Tenant.dto;
 
 
import lombok.*;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationSignalDTO {
 
    private String type;
    private Long userId;
 
}
 