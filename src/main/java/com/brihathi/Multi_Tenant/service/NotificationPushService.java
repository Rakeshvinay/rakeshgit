package com.brihathi.Multi_Tenant.service;
 
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
 
import com.brihathi.Multi_Tenant.dto.NotificationResponseDTO;
import com.brihathi.Multi_Tenant.dto.NotificationSignalDTO;
 
import lombok.RequiredArgsConstructor;
 
 
 
@Service
@RequiredArgsConstructor
public class NotificationPushService {
 
    private final SimpMessagingTemplate messagingTemplate;
 
 
//     public void sendNotificationSignal(Long userId) {
 
//         NotificationSignalDTO signal =
//                 new NotificationSignalDTO("NEW_NOTIFICATION", userId);
 
//         messagingTemplate.convertAndSendToUser(
//                 userId.toString(),
//                 "/queue/notifications",
//                 signal
//         );
//     }
// public void sendNotificationSignal(Long userId) {
//         sendNotificationSignal(userId, "USER"); // fallback
//     }
public void sendNotificationSignal(Long id, String role) {

        NotificationSignalDTO signal =
                new NotificationSignalDTO("NEW_NOTIFICATION", id);
    
        messagingTemplate.convertAndSendToUser(
                role + "_" + id,   // ✅ IMPORTANT CHANGE
                "/queue/notifications",
                signal
        );
    }
    // Send DTO directly
    public void sendToUser(
            Long userId,
            List<NotificationResponseDTO> dtoList
    ) {
 
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                dtoList
        );
    }


    public void sendToUser(Long id, String role, List<NotificationResponseDTO> dtoList) {

        messagingTemplate.convertAndSendToUser(
                role + "_" + id,   // ✅ USER_9 / EDUCATOR_5
                "/queue/notifications",
                dtoList
        );
    }
}
 