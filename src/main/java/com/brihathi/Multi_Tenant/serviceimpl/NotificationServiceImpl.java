package com.brihathi.Multi_Tenant.serviceimpl;
 
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import javax.management.Notification;
 
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.brihathi.Multi_Tenant.dto.StudentResultDTO;
import com.brihathi.Multi_Tenant.dto.ExamResultsDashboardDTO;
import com.brihathi.Multi_Tenant.dto.NotificationGroupedResponseDTO;
import com.brihathi.Multi_Tenant.dto.NotificationResponseDTO;
import com.brihathi.Multi_Tenant.entity.Notifications;
import com.brihathi.Multi_Tenant.entity.ScheduledExam;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.Educator;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.context.TenantContext;
// import com.brihathi.Multi_Tenant.enums.Category;
import com.brihathi.Multi_Tenant.enums.NotificationStatus;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.repository.NotificationRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.EducatorRepository;
import com.brihathi.Multi_Tenant.service.NotificationPushService;
import com.brihathi.Multi_Tenant.service.NotificationService;
import com.brihathi.Multi_Tenant.service.TenantService;
import com.brihathi.Multi_Tenant.service.DashboardService;
// import com.brihathi.Multi_Tenant.service.EducatorDashboardService;
import com.brihathi.Multi_Tenant.service.NotificationPushService;
import com.brihathi.Multi_Tenant.mapper.*;
 
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
 
    private final NotificationRepository repository;
    private final NotificationPushService pushService;
    private final NotificationMapper mapper;
    private final ScheduledExamRepository scheduledExamRepository;
    private final TenantService tenantService;
    private final TenantRepository tenantRepository;
    private final EducatorRepository educatorRepository;
    private final TenantContext tenantContext;
//     private final EducatorDashboardService dashboardService;
    private final NotificationPushService webSocketService;
 
   
       
 
@Override
public void createExamNotifications(
 
        Long tenantId,
        Long userId,
        Long educatorId,
        Long eduScheduledExamId,
        Long scheduledExamId,
 
        String batchId,
        String branchId,
 
        // Category category,
        Subject examName,
        Long totalDuration,
 
        LocalDateTime scheduledTime,
        LocalDate scheduledDate,
        LocalDateTime endTime
) {
 
    System.out.println("Creating notifications for userId: " + userId);
 
 
 
    // Before Start
    createNotification(
            tenantId,
            userId,
            educatorId,
 
            eduScheduledExamId,
            scheduledExamId,
 
            batchId,
            branchId,
 
            "Exam Starting Soon",
            "Your exam will start in 2 minutes",
 
        //     category,
            examName,
 
            scheduledTime,
            scheduledDate,
            endTime
    );
 
 
 
}
 
 
@Override
public void createEndExamNotifications(
 
        Long tenantId,
        Long userId,
        Long educatorId,
        Long eduScheduledExamId,
        Long scheduledExamId,
 
        String batchId,
        String branchId,
 
        // Category category,
        Subject examName,
        Long totalDuration,
 
        LocalDateTime scheduledTime,
        LocalDate scheduledDate,
        LocalDateTime endTime
) {
 
    System.out.println("Creating notifications for userId: " + userId);
 
 
 
 
    // Before End
    createNotification(
            tenantId,
            userId,
            educatorId,
 
            eduScheduledExamId,
            scheduledExamId,
 
            batchId,
            branchId,
 
            "Exam Ending Soon",
            "Your exam will end in 2 minutes",
 
        //     category,
            examName,
 
            scheduledTime,
            scheduledDate,
            endTime
    );
}
 
 
@Override
public void createNotification(
 
        Long tenantId,
        Long userId,
        Long educatorId,
 
        Long eduScheduledExamId,
        Long scheduledExamId,
 
        String batchId,
        String branchId,
 
        String title,
        String message,
 
        // Category category,
        Subject examType,
 
        LocalDateTime scheduleTime,
        LocalDate scheduledDate,
        LocalDateTime endTime
) {
 
    // Combine date + time
    LocalDateTime scheduledDateTime =
            LocalDateTime.of(scheduledDate, scheduleTime.toLocalTime());
 
    LocalDateTime now = LocalDateTime.now();
 
    System.out.println("ScheduleTime: " + scheduledDateTime);
    System.out.println("CurrentTime : " + now);
 
 
 
    // 3️⃣ Create Notification
    Notifications notification =
            Notifications.builder()
 
                    .tenantId(tenantId)
                    .userId(userId)
                    .educatorId(educatorId)
 
                    .eduScheduledExamId(eduScheduledExamId)
                    .scheduledExamId(scheduledExamId)
 
                    .batchId(batchId)
                    .branchId(branchId)
 
                    .title(title)
                    .message(message)
 
                //     .category(category)
                    .examType(examType)
 
                    .scheduleTime(scheduleTime.toLocalTime())
                    .scheduledDate(scheduledDate)
                    .endTime(endTime.toLocalTime())
 
                    .status(NotificationStatus.PENDING)
                    .retryCount(0)
 
                    .build();
 
    // 4️⃣ Save
    repository.save(notification);
    // webSocketService.sendNotificationSignal(userId);
    webSocketService.sendNotificationSignal(userId, "USER");
 
    System.out.println("Saved: " + title);
}
 
 
@Override
@Transactional
public void sendExamScheduledNotification(
 
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
) {
 
    String title = "Exam Scheduled";
 
    String message =
            "Your exam '" + examName +
            "' is scheduled at " + scheduledTime +
            " on " + scheduledDate;
 
    // Combine date + time
//     LocalDateTime scheduleDateTime =
//             LocalDateTime.of(scheduledDate, scheduledTime);
LocalDateTime scheduleDateTime = scheduledTime;
 
 
 
    // 1️⃣ CREATE NOTIFICATION (PENDING)
    Notifications notification =
            Notifications.builder()
 
                .tenantId(tenantId)
                .userId(userId)
                .educatorId(educatorId)
 
                .eduScheduledExamId(eduScheduledExamId)
                .scheduledExamId(scheduledExamId)
 
                .batchId(batchId)
                .branchId(branchId)
 
                .title(title)
                .message(message)
                .examType(examName)
                // .category(category)
                // .totalDuration(totalDuration)
                .totalDuration(String.valueOf(totalDuration))
 
                // .totalDuration(formatDuration(totalDuration))
 
 
                .scheduleTime(scheduledTime.toLocalTime())
 
                .scheduledDate(scheduledDate)
                .endTime(endTime.toLocalTime())    
 
                .status(NotificationStatus.PENDING)
 
                .retryCount(0)
 
                .build();
 
 
    // 2️⃣ SAVE + FLUSH
    Notifications saved =
            repository.saveAndFlush(notification);
            // webSocketService.sendNotificationSignal(userId);
            webSocketService.sendNotificationSignal(userId, "USER");
       
            System.out.println("Saved notification with ID: " + saved.getId() + " and status: " + saved.getStatus() + " and examName: " + saved.getExamType() + " and totalDuration: " + saved.getTotalDuration());
       
 
 
}
 
private String formatDuration(Duration d) {
 
    if (d == null) return null;
 
    long hours = d.toHours();
    long minutes = d.toMinutes() % 60;
    long seconds = d.getSeconds() % 60;
 
    return String.format(
        "%02d:%02d:%02d",
        hours, minutes, seconds
    );
}
 
 
@Override
@Transactional
public NotificationGroupedResponseDTO pushPendingForUser(
        Long userId,
        HttpServletRequest request
) {
 
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
 
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }
 
    User authenticatedUser = (User) authentication.getPrincipal();
    Long jwtUserId = authenticatedUser.getUserId();
 
    if (!jwtUserId.equals(userId)) {
        throw new RuntimeException(
                "Unauthorized: User ID mismatch. JWT=" + jwtUserId +
                ", Requested=" + userId
        );
    }
 
//     String subdomain = tenantService.getsubdomain(request);
String subdomain = extractSubdomain(request);
 
    Tenant tenant = tenantRepository
            .findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));
 
    Long tenantId = tenant.getTenantId();
 
    List<NotificationStatus> statuses = Arrays.asList(
            NotificationStatus.PENDING,
            NotificationStatus.SENT
    );
 
    List<Notifications> notifications =
            repository.findByUserIdAndTenantIdAndStatusInOrderByCreatedAtAsc(
                    userId,
                    tenantId,
                    statuses
            );
 
    List<NotificationResponseDTO> pendingList = new ArrayList<>();
    List<NotificationResponseDTO> sentList = new ArrayList<>();
 
    for (Notifications n : notifications) {
 
        NotificationResponseDTO dto = mapToDTO(n);
 
        if (n.getStatus() == NotificationStatus.PENDING) {
 
            pendingList.add(dto);
 
            // mark as sent
            n.setStatus(NotificationStatus.SENT);
 
        } else if (n.getStatus() == NotificationStatus.SENT) {
 
            sentList.add(dto);
        }
    }
 
        repository.saveAll(notifications);
 
    NotificationGroupedResponseDTO response = new NotificationGroupedResponseDTO();
 
    NotificationGroupedResponseDTO.NewNotifications newSection =
            new NotificationGroupedResponseDTO.NewNotifications();
 
    newSection.setCount(pendingList.size());
    newSection.setNotifications(pendingList);
 
    NotificationGroupedResponseDTO.SentNotifications sentSection =
            new NotificationGroupedResponseDTO.SentNotifications();
 
    sentSection.setNotifications(sentList);
 
    response.setNewNotifications(newSection);
    response.setSent(sentSection);
 
    return response;
}
 
private NotificationResponseDTO mapToDTO(Notifications n) {
 
    NotificationResponseDTO dto = new NotificationResponseDTO();
 
    dto.setId(n.getId());
    dto.setUserId(n.getUserId());
    dto.setTitle(n.getTitle());
    dto.setExamType(n.getExamType());
//     dto.setCategory(n.getCategory());
    dto.setMessage(n.getMessage());
 
    dto.setScheduleTime(n.getScheduleTime());
    dto.setScheduledDate(n.getScheduledDate());
    dto.setEndTime(n.getEndTime());
 
    dto.setTotalDuration(n.getTotalDuration());
    dto.setCreatedAt(n.getCreatedAt());
 
 
    return dto;
}
@Override
public void createCancelExamNotification(
 
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
) {
 
    String title = "Exam Cancelled";
    String message = "Your scheduled " + examType + " " + " has been cancelled.";
 
    // Avoid duplicate
    if (repository.existsByUserIdAndScheduledExamIdAndTitle(
            userId, scheduledExamId, title)) {
        return;
    }
 
    Notifications notification =
            Notifications.builder()
 
                    .tenantId(tenantId)
                    .userId(userId)
                    .educatorId(educatorId)
 
                    .eduScheduledExamId(eduScheduledExamId)
                    .scheduledExamId(scheduledExamId)
 
                    .batchId(batchId)
                    .branchId(branchId)
 
                    .title(title)
                    .message(message)
 
                //     .category(category)
                    .examType(examType)
 
                    .scheduledDate(date)
                    .scheduleTime(time.toLocalTime())
                    .endTime(endTime.toLocalTime())
 
                    .status(NotificationStatus.PENDING)
                    .retryCount(0)
 
                    .build();
 
    repository.save(notification);
    // webSocketService.sendNotificationSignal(userId);
    webSocketService.sendNotificationSignal(userId, "USER");
}
 
 
 
@Override
public void createUserProfileUpdateNotification(
        Long tenantId,
        Long userId,
        String branch,
        String batch
) {
 
    String title = "Profile Updated";
    String message = "Your profile information has been updated successfully.";
 
    Notifications notification = Notifications.builder()
            .tenantId(tenantId)
            .userId(userId)
            .branchId(branch)
            .batchId(batch)
            .title(title)
            .message(message)
            .status(NotificationStatus.PENDING)
            .retryCount(0)
            .build();
 
        repository.save(notification);
        webSocketService.sendNotificationSignal(userId, "USER");
 
        System.out.println("Profile update notification created for user: " + userId);
}
 
 
// @Override
// public void createEducatorProfileUpdateNotification(
 
//         Long tenantId,
//         Long educatorId
// ) {
 
//     String title = "Profile Updated";
//     String message = "Your profile information has been updated successfully.";
 
//     // Prevent duplicate
//     if (repository.existsByUserIdAndTitle(educatorId, title)) {
//         return;
//     }
 
//     Notifications notification =
//             Notifications.builder()
 
//                     .tenantId(tenantId)
 
//                     // For educators also stored in userId column
//                     .userId(educatorId)
 
                   
 
//                     .title(title)
//                     .message(message)
 
//                     .status(NotificationStatus.PENDING)
//                     .retryCount(0)
 
//                     .build();
 
//     repository.save(notification);
//     //webSocketService.sendNotificationSignal(userId);
//     webSocketService.sendNotificationSignal(educatorId, "EDUCATOR");
 
//     System.out.println(
//         "Educator profile notification created: " + educatorId
//     );
// }



// @Override
// public void createEducatorProfileUpdateNotification(
//         Long tenantId,
//         Long educatorId
// ) {

//     String title = "Profile Updated";
//     String message = "Your profile information has been updated successfully.";

//     // ❌ REMOVE WRONG CHECK (temporary)
//     // if (repository.existsByUserIdAndTitle(educatorId, title)) {
//     //     return;
//     // }

//     Notifications notification =
//             Notifications.builder()
//                     .tenantId(tenantId)

//                     // ✅ CORRECT FIELD
//                     .educatorId(educatorId)

//                     .title(title)
//                     .message(message)

//                     .status(NotificationStatus.PENDING)
//                     .retryCount(0)
//                     .build();

//     Notifications saved = repository.save(notification);

//     System.out.println("✅ Saved educator notification ID: " + saved.getId());

//     // ✅ WebSocket push
//     webSocketService.sendNotificationSignal(educatorId, "EDUCATOR");

//     System.out.println(
//         "🚀 Educator profile notification created: " + educatorId
//     );
// }
 
@Override
public void createScheduledExamAbortedNotification(
 
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
) {
 
    String title = "Exam Aborted";
 
    String message =
            "Your educator scheduled " + examName + " exam has been aborted. Please contact your educator for details.";
 
    // Prevent duplicates
    if (repository.existsByUserIdAndScheduledExamIdAndTitle(
            userId, scheduledExamId, title)) {
 
        return;
    }
 
    Notifications notification =
            Notifications.builder()
 
                    .tenantId(tenantId)
                    .userId(userId)
                    .educatorId(educatorId)
                //     .category(category)
                    .examType(examName)
 
                    .eduScheduledExamId(eduScheduledExamId)
                    .scheduledExamId(scheduledExamId)
 
                    .batchId(batchId)
                    .branchId(branchId)
 
                    .title(title)
                    .message(message)
 
                    .status(NotificationStatus.PENDING)
                    .retryCount(0)
                    // .scheduledDate(date)
                    // .scheduleTime(time)
                    // .endTime(endTime)
 
                    .build();
 
    repository.save(notification);
    // webSocketService.sendNotificationSignal(userId);
    webSocketService.sendNotificationSignal(userId, "USER");
 
    System.out.println(
        "Exam aborted notification created for user: " + userId
    );
}
 
 
 
@Override
public void createExamAbortedNotification(
 
        Long tenantId,
        Long userId,
        Long examId,
        Subject examName,
        // Category category,
        String totalDuration
 
) {
 
    String title = "Exam Aborted";
 
    String message =
            "Your" + examName + " exam has been aborted. Due to malpractice attempts.";
 
    // Prevent duplicates
    if (repository.existsByUserIdAndExamIdAndTitle(
            userId, examId, title)) {
 
        return;
    }
 
    Notifications notification =
            Notifications.builder()
 
                    .tenantId(tenantId)
                    .userId(userId)
                    .examId(examId)
                    .totalDuration(totalDuration)
                //     .category(category)
                    .examType(examName)
 
 
                    .title(title)
                    .message(message)
 
                    .status(NotificationStatus.PENDING)
                    .retryCount(0)
                    // .scheduledDate(date)
                    // .scheduleTime(time)
                    // .endTime(endTime)
 
                    .build();
 
    repository.save(notification);
    // webSocketService.sendNotificationSignal(userId);
    webSocketService.sendNotificationSignal(userId, "USER");
 
    System.out.println(
        "Exam aborted notification created for normal flow user: " + userId
    );
}
 
 
private String extractSubdomain(HttpServletRequest request) {
 
        String host = request.getServerName();
        // examples:
        // brihathi.localhost
        // brihathi.neetswan.ai
   
        // ✅ Local development support
        if (host.endsWith("localhost")) {
            return host.split("\\.")[0]; // brihathi
        }
   
        // ✅ Production subdomain support
        String[] parts = host.split("\\.");
   
        if (parts.length < 3) {
            throw new RuntimeException("Invalid tenant subdomain: " + host);
        }
   
        return parts[0];
    }


    public NotificationGroupedResponseDTO pushPendingForEducator(
        Long educatorId,
        HttpServletRequest request
) {

    // String subdomain = tenantService.getSubdomain(request);
    // Tenant tenant = tenantService.findBySubdomain(subdomain);
     // 1️⃣ Resolve tenant from subdomain
 
     String subdomain = tenantContext.getTenant();
 
     if (subdomain == null) {

         throw new RuntimeException("Tenant not found in request");

     }

     Tenant tenant = tenantRepository.findBySubdomain(subdomain)

             .orElseThrow(() -> new RuntimeException("Invalid tenant"));


    Long tenantId = tenant.getTenantId();

    List<NotificationStatus> statuses =
            List.of(NotificationStatus.PENDING, NotificationStatus.SENT);

    // List<Notifications> notifications =
    //         repository.findByEducatorIdAndTenantIdAndStatusInOrderByCreatedAtAsc(
    //                 educatorId,
    //                 tenantId,
    //                 statuses
    //         );
    List<Notifications> notifications =
        repository.findByEducatorIdAndUserIdIsNull(educatorId);

    List<NotificationResponseDTO> dtoList =
            notifications.stream()
                    .map(mapper::toDto)
                    .toList();

    // Separate NEW vs SENT
    List<NotificationResponseDTO> newList =
            notifications.stream()
                    .filter(n -> n.getStatus() == NotificationStatus.PENDING)
                    .map(mapper::toDto)
                    .toList();

    List<NotificationResponseDTO> sentList =
            notifications.stream()
                    .filter(n -> n.getStatus() == NotificationStatus.SENT)
                    .map(mapper::toDto)
                    .toList();

    // Mark as SENT
    notifications.forEach(n -> n.setStatus(NotificationStatus.SENT));
    repository.saveAll(notifications);

    // Push via WebSocket
    webSocketService.sendToUser(
            educatorId,
            "EDUCATOR",
            dtoList
    );

    // Build response
    NotificationGroupedResponseDTO response =
            new NotificationGroupedResponseDTO();

    NotificationGroupedResponseDTO.NewNotifications newNotifications =
            new NotificationGroupedResponseDTO.NewNotifications();

    newNotifications.setCount(newList.size());
    newNotifications.setNotifications(newList);

    NotificationGroupedResponseDTO.SentNotifications sent =
            new NotificationGroupedResponseDTO.SentNotifications();

    sent.setNotifications(sentList);

    response.setNewNotifications(newNotifications);
    response.setSent(sent);

    return response;
}

@Override
public void createEducatorProfileUpdateNotification(
        Long tenantId,
        Long educatorId
) {

    String title = "Profile Updated";
    String message = "Your profile information has been updated successfully.";

    // ✅ Prevent duplicates (KEY FIX)
    if (repository.existsByEducatorIdAndTitleAndCreatedAtAfter(
            educatorId,
            title,
            LocalDateTime.now().minusSeconds(30)
    )) {
        return;
    }

    Notifications notification =
            Notifications.builder()
                    .tenantId(tenantId)
                    .educatorId(educatorId)
                    .title(title)
                    .message(message)
                    .status(NotificationStatus.PENDING)
                    .retryCount(0)
                    .build();

    repository.save(notification);

    webSocketService.sendNotificationSignal(educatorId, "EDUCATOR");

    System.out.println("✅ Educator notification created: " + educatorId);
}


// @Override
// public void createExamParticipationNotificationForEducators(
//         Long tenantId,
//         String subject,
//         Long eduScheduledExamId,
//         // String branch,
//         // String batch,
//         int totalStudents,
//         int presentStudents,
//         int absentStudents
// ) {

//     String title = "Exam Participation Report";

//     String message =
//             subject + " exam completed. " +
//             "Present: " + presentStudents +
//             " / " + totalStudents +
//             " | Absent: " + absentStudents;

// //     // 🔥 Get ALL educators of tenant
// //     List<Educator> educators =
// //             educatorRepository.findByTenantId(tenantId);

// //     for (Educator educator : educators) {

// //         // ✅ prevent duplicates (important)
// //         if (repository.existsByEducatorIdAndTitleAndCreatedAtAfter(
// //                 educator.getEducatorId(),
// //                 title,
// //                 LocalDateTime.now().minusMinutes(1)
// //         )) {
// //             continue;
// //         }

// //         Notifications notification =
// //                 Notifications.builder()
// //                         .tenantId(tenantId)
// //                         .educatorId(educator.getEducatorId())
// //                         .eduScheduledExamId(eduScheduledExamId)
// //                         // .branchId(branch)
// //                         // .batchId(batch)
// //                         .title(title)
// //                         .message(message)
// //                         .examType(Subject.valueOf(subject))
// //                         .status(NotificationStatus.PENDING)
// //                         .retryCount(0)
// //                         .build();

// //         repository.save(notification);

// //         // ✅ push via websocket
// //         webSocketService.sendNotificationSignal(
// //                 educator.getEducatorId(),
// //                 "EDUCATOR"
// //         );
// //     }
// boolean alreadySent = repository
//         .existsByTenantIdAndTitleAndCreatedAtAfter(
//                 tenantId,
//                 title,
//                 LocalDateTime.now().minusMinutes(5)
//         );

// if (alreadySent) {
//     return;
// }

// List<Educator> educators = educatorRepository.findByTenantId(tenantId);

// for (Educator educator : educators) {

//     Notifications notification =
//             Notifications.builder()
//                     .tenantId(tenantId)
//                     .educatorId(educator.getEducatorId())
//                     .title(title)
//                     .message(message)
//                     .status(NotificationStatus.PENDING)
//                     .retryCount(0)
//                     .build();

//     repository.save(notification);

//     webSocketService.sendNotificationSignal(
//             educator.getEducatorId(),
//             "EDUCATOR"
//     );
// }

//     System.out.println("✅ Exam participation notification sent to all educators");
// }



@Override
public void createExamParticipationNotificationForEducators(
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
) {

    String title = "Exam Participation Report";

//     // 🔹 BASE MESSAGE
//     StringBuilder message = new StringBuilder();
//     message.append(subject)
//             .append(" exam completed.\n")
//             .append("Present: ").append(presentStudents)
//             .append(" / ").append(totalStudents)
//             .append(" | Absent: ").append(absentStudents);

//     // =====================================================
//     // 🔥 GET TOP + LOW PERFORMERS
//     // =====================================================

//     try {
//         ExamResultsDashboardDTO report =
//                 dashboardService.getExamResults(eduScheduledExamId);

//         List<StudentResultDTO> students = report.getStudentResults();

//         if (students != null && !students.isEmpty()) {

//             // 🔹 TOP 3
//             message.append("\n\n🏆 Top Performers:\n");

//             for (int i = 0; i < Math.min(3, students.size()); i++) {
//                 StudentResultDTO s = students.get(i);
//                 message.append(i + 1)
//                         .append(". ")
//                         .append(s.getStudentName())
//                         .append(" - ")
//                         .append(s.getScore())
//                         .append("\n");
//             }

//             // 🔹 LAST 3
//             message.append("\n⚠️ Low Performers:\n");

//             int size = students.size();
//             for (int i = size - 1; i >= Math.max(size - 3, 0); i--) {
//                 StudentResultDTO s = students.get(i);
//                 message.append("- ")
//                         .append(s.getStudentName())
//                         .append(" - ")
//                         .append(s.getScore())
//                         .append("\n");
//             }
//         }

//     } catch (Exception e) {
//         System.out.println("❌ Failed to fetch performers: " + e.getMessage());
//     }
StringBuilder message = new StringBuilder();

message.append(subject)
       .append(" exam completed.\n")
       .append("Present: ").append(presentStudents)
       .append(" / ").append(totalStudents)
       .append(" | Absent: ").append(absentStudents);

// TOP
message.append("\n\n🏆 Top Performers:\n");
for (int i = 0; i < topPerformers.size(); i++) {
    StudentResultDTO s = topPerformers.get(i);
    message.append(i + 1).append(". ")
           .append(s.getStudentName())
           .append(" - ").append(s.getScore())
           .append("\n");
}

// LOW
message.append("\n⚠️ Low Performers:\n");
for (StudentResultDTO s : lowPerformers) {
    message.append("- ")
           .append(s.getStudentName())
           .append(" - ").append(s.getScore())
           .append("\n");
}
    // =====================================================

    // 🔥 DUPLICATE CHECK (OUTSIDE LOOP)
    boolean alreadySent = repository
            .existsByTenantIdAndTitleAndCreatedAtAfter(
                    tenantId,
                    title,
                    LocalDateTime.now().minusMinutes(5)
            );

    if (alreadySent) return;

    List<Educator> educators = educatorRepository.findByTenantId(tenantId);

    for (Educator educator : educators) {

        Notifications notification =
                Notifications.builder()
                        .tenantId(tenantId)
                        .educatorId(educator.getEducatorId())
                        .eduScheduledExamId(eduScheduledExamId)
                        .title(title)
                        .message(message.toString()) // ✅ FULL MESSAGE
                        .status(NotificationStatus.PENDING)
                        .retryCount(0)
                        .build();

        repository.save(notification);

        webSocketService.sendNotificationSignal(
                educator.getEducatorId(),
                "EDUCATOR"
        );
    }

    System.out.println("✅ Participation + Top/Low performers notification sent");
}

// delete single notification
@Override
public void deleteNotification(Long id) {
       repository.deleteById(id);
   }

   // clear all notifications of a user
@Override
 public void clearAllNotifications(Long userId) {
       repository.deleteByUserId(userId);
   }
   // clear all notifications of a user
@Override
 public void clearAllEducatorNotifications(Long educatorId) {
       repository.deleteByEducatorId(educatorId);
   }
}
 
 