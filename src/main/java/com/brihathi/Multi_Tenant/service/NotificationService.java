
 
 
package com.brihathi.Multi_Tenant.service;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
 
import com.brihathi.Multi_Tenant.dto.NotificationGroupedResponseDTO;
import com.brihathi.Multi_Tenant.dto.NotificationResponseDTO;
import com.brihathi.Multi_Tenant.dto.StudentResultDTO;
// import com.brihathi.Multi_Tenant.enums.Category;
import com.brihathi.Multi_Tenant.enums.Subject;
 
import jakarta.servlet.http.HttpServletRequest;
 
import java.time.Duration;
 
public interface NotificationService {
 
    /**
     * Create all exam related notifications
     */
    void createExamNotifications(
 
            Long tenantId,
            Long userId,
            Long educatorId,
 
            Long eduScheduledExamId,
            Long scheduledExamId,
 
            String batchId,
            String branchId,
        //     Category category,
                   Subject examName,
                   Long totalDuration,
 
                   LocalDateTime scheduledTime,
            LocalDate scheduledDate,
            LocalDateTime endTime
    );
 
 
    void createEndExamNotifications(
 
            Long tenantId,
            Long userId,
            Long educatorId,
 
            Long eduScheduledExamId,
            Long scheduledExamId,
 
            String batchId,
            String branchId,
        //     Category category,
                   Subject examName,
                   Long totalDuration,
 
                   LocalDateTime scheduledTime,
            LocalDate scheduledDate,
            LocalDateTime endTime
    );
 
 
    /**
     * Create single scheduled notification
     */
    void createNotification(
 
            Long tenantId,
            Long userId,
            Long educatorId,
 
            Long eduScheduledExamId,
            Long scheduledExamId,
 
            String batchId,
            String branchId,
 
            String title,
            String message,
        //     Category category,
            Subject examName,
 
            LocalDateTime scheduleTime,
            LocalDate scheduledDate,
            LocalDateTime endTime
    );
 
 
    void sendExamScheduledNotification(
 
        Long tenantId,
        Long userId,
        Long educatorId,
 
        Long eduScheduledExamId,
        Long scheduledExamId,
 
        String batchId,
        String branchId,
 
        Subject examName,
        // Category category,
        Long totalDuration,
        LocalDateTime scheduledTime,
        LocalDate scheduledDate,
        LocalDateTime endTime
);
 
//List<NotificationResponseDTO> pushPendingForUser(Long userId,HttpServletRequest request);
 
 
void createCancelExamNotification(
 
        Long tenantId,
        Long userId,
        Long educatorId,
 
        Long eduScheduledExamId,
        Long scheduledExamId,
 
        String batchId,
        String branchId,
 
        // Category category,
        Subject examType,
 
        LocalDate date,
        LocalDateTime time,
        LocalDateTime endTime
);
 
 
void createUserProfileUpdateNotification(
        Long tenantId,
        Long userId,
        String branch,
        String batch
);
 
void createEducatorProfileUpdateNotification(
        Long tenantId,
        Long educatorId
);
 
void createScheduledExamAbortedNotification(
 
        Long tenantId,
        Long userId,
        Long educatorId,
        Subject examName,
        // Category category,
 
        Long eduScheduledExamId,
        Long scheduledExamId,
 
        String batchId,
        String branchId
        // LocalDate date,
        // LocalTime time,
        // LocalTime endTime
);
 
 
void createExamAbortedNotification(
 
        Long tenantId,
        Long userId,
        Long examId,
        Subject examName,
        // Category category,
        String totalDuration
 
        // LocalDate date,
        // LocalTime time,
        // LocalTime endTime
);
//List<NotificationResponseDTO> pushPendingNotifications(Long userId, HttpServletRequest request);
 
NotificationGroupedResponseDTO pushPendingForUser(Long userId, HttpServletRequest request);
 
//List<NotificationResponseDTO> pushPendingForUser(Long userId, HttpServletRequest request);

NotificationGroupedResponseDTO pushPendingForEducator(
        Long educatorId,
        HttpServletRequest request
);


void createExamParticipationNotificationForEducators(
        Long tenantId,
        String subject,
        Long eduScheduledExamId,
        // String branch,
        // String batch,
        int totalStudents,
        int presentStudents,
        int absentStudents,
        List<StudentResultDTO> topPerformers,
        List<StudentResultDTO> lowPerformers
);
void deleteNotification(Long id);
void  clearAllNotifications(Long userId);
void  clearAllEducatorNotifications(Long educatorId);
}
 
 