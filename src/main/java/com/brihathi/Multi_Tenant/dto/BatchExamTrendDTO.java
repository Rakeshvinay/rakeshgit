package com.brihathi.Multi_Tenant.dto;
 
 
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
@Data
@Builder
public class BatchExamTrendDTO {
 
    private Long eduScheduledExamId;
    private String branch;
    private String batch;
    private LocalDateTime scheduledTime;
    private LocalDate scheduledDate;
 
    private Integer noOfStudentsScheduled;
    private Integer noOfStudentsAttempted;
    private Integer noOfStudentsUnattempted;
 
    private Double percentage;
    private Double diffFromPrevious;
}
 
 
 