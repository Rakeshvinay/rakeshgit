package com.brihathi.Multi_Tenant.controller;
 
import java.util.List;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.brihathi.Multi_Tenant.dto.NotificationGroupedResponseDTO;
import com.brihathi.Multi_Tenant.dto.NotificationResponseDTO;
import com.brihathi.Multi_Tenant.service.NotificationPushService;
import com.brihathi.Multi_Tenant.service.NotificationService;
 
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notification-push")
@RequiredArgsConstructor
public class NotificationPushController {
 
    private final NotificationPushService pushService;
    private final NotificationService notificationService;
 
 
    @GetMapping("/send/{userId}")
public ResponseEntity<NotificationGroupedResponseDTO> sendToUser(
        @PathVariable Long userId,
        HttpServletRequest request
) {
 
    NotificationGroupedResponseDTO result =
            notificationService.pushPendingForUser(userId, request);
 
    return ResponseEntity.ok(result);
}

@GetMapping("/send/educator/{educatorId}")
public ResponseEntity<NotificationGroupedResponseDTO> sendToEducator(
        @PathVariable Long educatorId,
        HttpServletRequest request
) {

    NotificationGroupedResponseDTO result =
            notificationService.pushPendingForEducator(educatorId, request);

    return ResponseEntity.ok(result);
}

// @GetMapping("/test-notification/{userId}")
// public String test(@PathVariable Long userId) {

//     notificationService.createUserProfileUpdateNotification(
//             1L, userId, "testBranch", "testBatch"
//     );

//     return "triggered";
// }

 // Delete particular notification
 @DeleteMapping("/delete/{id}")
 public ResponseEntity<String> deleteNotification(@PathVariable Long id) {

     notificationService.deleteNotification(id);

     return ResponseEntity.ok("Notification deleted successfully");
 }

 // Clear all notifications for a user
 @DeleteMapping("/clear/{userId}")
 public ResponseEntity<String> clearUserNotifications(@PathVariable Long userId) {

     notificationService.clearAllNotifications(userId);

     return ResponseEntity.ok("All notifications cleared");
 }
 // Clear all notifications for a user
 @DeleteMapping("/clear/educator/{educatorId}")
 public ResponseEntity<String> clearEducatorNotifications(@PathVariable Long educatorId) {

     notificationService.clearAllEducatorNotifications(educatorId);

     return ResponseEntity.ok("All notifications cleared");
 }
}
 
 