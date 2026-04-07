package com.brihathi.Multi_Tenant.dto;
 
import java.time.LocalDateTime;
 
public interface PreviousTestDTO {
    LocalDateTime getEndDate();
    String getExamType();
    int getMarks();
    float getPercentage();
    int getExamId();
    String getDifficulty();
}
 
