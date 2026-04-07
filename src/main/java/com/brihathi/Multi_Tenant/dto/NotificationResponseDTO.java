package com.brihathi.Multi_Tenant.dto;
 
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
 
// import com.brihathi.Multi_Tenant.enums.Category;
import com.brihathi.Multi_Tenant.enums.Subject;
 
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class NotificationResponseDTO {
 
    private Long userId;
    private Long id;
 
    private String title;
 
    private Subject examType;
 
    // private Category category;
 
    private String message;
 
    private LocalTime scheduleTime;
 
    private LocalDate scheduledDate;
 
    private LocalTime endTime;
 
    private String totalDuration;
 
    private LocalDateTime createdAt;
}
 
 