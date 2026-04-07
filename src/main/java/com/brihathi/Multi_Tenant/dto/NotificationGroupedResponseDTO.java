package com.brihathi.Multi_Tenant.dto;
 
import lombok.Getter;
import lombok.Setter;
import java.util.List;
 
@Getter
@Setter
public class NotificationGroupedResponseDTO {
 
    private NewNotifications newNotifications;
    private SentNotifications sent;
 
    @Getter
    @Setter
    public static class NewNotifications {
        private int count;
        private List<NotificationResponseDTO> notifications;
    }
 
    @Getter
    @Setter
    public static class SentNotifications {
        private List<NotificationResponseDTO> notifications;
    }
}
 