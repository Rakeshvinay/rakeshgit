package com.brihathi.Multi_Tenant.mapper;
 
import org.springframework.stereotype.Component;
 
import com.brihathi.Multi_Tenant.dto.NotificationResponseDTO;
import com.brihathi.Multi_Tenant.entity.Notifications;
 
@Component
public class NotificationMapper {
 
    public NotificationResponseDTO toDto(Notifications n) {
 
        NotificationResponseDTO dto =
                new NotificationResponseDTO();
 
        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setTitle(n.getTitle());
        dto.setExamType(n.getExamType());
        // dto.setCategory(n.getCategory());
        dto.setMessage(n.getMessage());
        dto.setScheduleTime(n.getScheduleTime());
        dto.setScheduledDate(n.getScheduledDate());
        dto.setEndTime(n.getEndTime());
        dto.setTotalDuration(n.getTotalDuration());
        dto.setCreatedAt(n.getCreatedAt());

        return dto;
    }
}
 
 