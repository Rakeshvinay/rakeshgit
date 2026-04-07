package com.brihathi.Multi_Tenant.scheduler;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
 
 
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
import com.brihathi.Multi_Tenant.dto.*;
import com.brihathi.Multi_Tenant.entity.Educator;
import com.brihathi.Multi_Tenant.entity.EducatorScheduledExam;
import com.brihathi.Multi_Tenant.entity.ScheduledExam;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Exam;
// import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus;
// import com.brihathi.Multi_Tenant.entity.Exam.ExamStatus status2;
// import com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus;
// import com.brihathi.Multi_Tenant.enums.Category;
import com.brihathi.Multi_Tenant.repository.EducatorRepository;
import com.brihathi.Multi_Tenant.repository.EducatorScheduledExamRepository;
// import com.brihathi.Multi_Tenant.repository.ExamAdvancedRepository;
import com.brihathi.Multi_Tenant.repository.ExamRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.NotificationRepository;
import com.brihathi.Multi_Tenant.service.NotificationService;
 
import lombok.RequiredArgsConstructor;
 
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
 
    private final EducatorScheduledExamRepository educatorRepo;
    private final ScheduledExamRepository studentRepo;
        private final ExamRepository examRepo;
//     private final ExamAdvancedRepository examAdvancedRepository;
//  private final ScheduledExamAdvancedRepository studentAdvancedRepo;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final EducatorRepository educatorRepository;
    private final NotificationRepository notificationRepository;
 
    // @Scheduled(cron = "0 * * * * *") // Every minute
    // @Scheduled(fixedRate = 5000)
    // public void generateNotifications() {
 
    //     System.out.println("Notification scheduler running...");
 
    //     LocalDate today = LocalDate.now();
    //     System.out.println("Today's date: " + today);
    //     LocalDateTime now = LocalDateTime.now();
    //     System.out.println("Current time: " + now);
    //     LocalDateTime next2Min = now.plusMinutes(2);
    //     System.out.println("Next 2 minutes: " + next2Min);
 
    //     // 1️⃣ Get upcoming exams (from educator table)
    //     List<EducatorScheduledExam> exams =
    //             educatorRepo.findUpcomingExams(
    //                     today,
    //                     now,
    //                     next2Min
    //             );
    //     // 1️⃣ Get upcoming exams (from educator table)
    //     List<EducatorScheduledExam> endingexams =
    //             educatorRepo.findEndingExams(
    //                     today,
    //                     now,
    //                     next2Min
    //             );
       
    //     for (EducatorScheduledExam exam : endingexams) {
 
    //         // 2️⃣ Get students for this exam
    //         List<ScheduledExam> students =
    //                 studentRepo.findByEduScheduledExamId(
    //                         exam.getEduScheduledExamId()
    //                 );
 
 
    //         // 3️⃣ Create notification per student
    //         for (ScheduledExam student : students) {
 
    //             notificationService.createEndExamNotifications(
 
    //                 student.getTenantId(),
    //                 student.getUserId(),
    //                 exam.getEducatorId(),
 
    //                 exam.getEduScheduledExamId(),
    //                 student.getScheduledExamId(),
 
    //                 student.getBatchId(),
    //                 student.getBranchId(),
 
    //             //     exam.getCategory(),
    //                 exam.getExamType(),
    //                 student.getTotalDuration(),
 
    //                 exam.getScheduledTime(),
    //                 exam.getScheduledDate(),
    //                 exam.getExamEndTime()
    //             );
    //         }
    //     }
   
               
 
    //     for (EducatorScheduledExam exam : exams) {
 
    //         // 2️⃣ Get students for this exam
    //         List<ScheduledExam> students =
    //                 studentRepo.findByEduScheduledExamId(
    //                         exam.getEduScheduledExamId()
    //                 );
 
 
    //         // 3️⃣ Create notification per student
    //         for (ScheduledExam student : students) {
 
    //             notificationService.createExamNotifications(
 
    //                 student.getTenantId(),
    //                 student.getUserId(),
    //                 exam.getEducatorId(),
 
    //                 exam.getEduScheduledExamId(),
    //                 student.getScheduledExamId(),
 
    //                 student.getBatchId(),
    //                 student.getBranchId(),
 
    //             //     exam.getCategory(),
    //                 exam.getExamType(),
    //                 student.getTotalDuration(),
 
    //                 exam.getScheduledTime(),
    //                 exam.getScheduledDate(),
    //                 exam.getExamEndTime()
    //             );
    //         }
    //     }
    // }
 
    @Scheduled(fixedRate = 5000)
    public void generateNotifications() {
    
        System.out.println("Notification scheduler running...");
    
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next2Min = now.plusMinutes(2);
    
        // 🔹 Upcoming exams
        List<EducatorScheduledExam> exams =
                educatorRepo.findUpcomingExams(today, now, next2Min);
    
        // 🔹 Ending exams
        List<EducatorScheduledExam> endingexams =
                educatorRepo.findEndingExams(today, now, next2Min);
    
        // =====================================================
        // 🔥 HANDLE ENDING EXAMS
        // =====================================================
        for (EducatorScheduledExam exam : endingexams) {
    
            List<ScheduledExam> students =
                    studentRepo.findByEduScheduledExamId(
                            exam.getEduScheduledExamId()
                    );
    
            for (ScheduledExam student : students) {
    
                // ✅ DUPLICATE CHECK (PER STUDENT + EXAM)
                boolean alreadySent =
                        notificationRepository.existsByUserIdAndScheduledExamIdAndTitle(
                                student.getUserId(),
                                student.getScheduledExamId(),
                                "Exam Ending Soon"
                        );
    
                if (alreadySent) continue;
    
                notificationService.createEndExamNotifications(
    
                        student.getTenantId(),
                        student.getUserId(),
                        exam.getEducatorId(),
    
                        exam.getEduScheduledExamId(),
                        student.getScheduledExamId(),
    
                        student.getBatchId(),
                        student.getBranchId(),
    
                        exam.getExamType(),
                        student.getTotalDuration(),
    
                        exam.getScheduledTime(),
                        exam.getScheduledDate(),
                        exam.getExamEndTime()
                );
            }
        }
    
        // =====================================================
        // 🔥 HANDLE UPCOMING EXAMS
        // =====================================================
        for (EducatorScheduledExam exam : exams) {
    
            List<ScheduledExam> students =
                    studentRepo.findByEduScheduledExamId(
                            exam.getEduScheduledExamId()
                    );
    
            for (ScheduledExam student : students) {
    
                // ✅ DUPLICATE CHECK (PER STUDENT + EXAM)
                boolean alreadySent =
                        notificationRepository.existsByUserIdAndScheduledExamIdAndTitle(
                                student.getUserId(),
                                student.getScheduledExamId(),
                                "Exam Starting Soon"
                        );
    
                if (alreadySent) continue;
    
                notificationService.createExamNotifications(
    
                        student.getTenantId(),
                        student.getUserId(),
                        exam.getEducatorId(),
    
                        exam.getEduScheduledExamId(),
                        student.getScheduledExamId(),
    
                        student.getBatchId(),
                        student.getBranchId(),
    
                        exam.getExamType(),
                        student.getTotalDuration(),
    
                        exam.getScheduledTime(),
                        exam.getScheduledDate(),
                        exam.getExamEndTime()
                );
            }
        }
    }
 
// @Scheduled(cron = "/5 * * * * *") // Every minute
@Scheduled(fixedRate = 5000)
public void generateUserProfileUpdateNotifications() {
 
    System.out.println("Checking profile updates...");
 
    LocalDateTime now = LocalDateTime.now();
 
    // Last 2 minute window
    // LocalDateTime twoMinutesAgo = now.minusMinutes(2);
    LocalDateTime oneSecondAgo = now.minusSeconds(5);
 
    // 1️⃣ Find recently updated users
    List<User> users = userRepository.findRecentlyUpdatedUsers(
            oneSecondAgo,
            // twoMinutesAgo,
            now
    );
 
    // 2️⃣ Create notification for each user
    for (User user : users) {
 
        notificationService.createUserProfileUpdateNotification(
                user.getTenantId(),
                user.getUserId(),
                user.getBranch(),
                user.getBatch()
        );
    }
}
 
 
    // @Scheduled(cron = "0 * * * * *") // Every minute
    @Scheduled(fixedRate = 5000)
public void generateCancelNotifications() {
 
    System.out.println("Checking cancelled exams...");
 
    // 1️⃣ Get cancelled exams
    // List<EducatorScheduledExam> cancelledExams =
    //         educatorRepo.findCancelledExams();
    LocalDateTime now = LocalDateTime.now();
    // LocalDateTime oneMinuteAgo = now.minusMinutes(1);
    LocalDateTime oneSecondAgo = now.minusSeconds(5);
 
    List<EducatorScheduledExam> cancelledExams =
            educatorRepo.findCancelledExams(oneSecondAgo,now);

 
 
    for (EducatorScheduledExam exam : cancelledExams) {
 
        // 2️⃣ Get students
        List<ScheduledExam> students =
                studentRepo.findByEduScheduledExamId(
                        exam.getEduScheduledExamId()
                );
 
 
        // 3️⃣ Notify each student
        for (ScheduledExam student : students) {
 
            notificationService.createCancelExamNotification(
 
                    student.getTenantId(),
                    student.getUserId(),
                    exam.getEducatorId(),
 
                    exam.getEduScheduledExamId(),
                    student.getScheduledExamId(),
 
                    student.getBatchId(),
                    student.getBranchId(),
 
                //     exam.getCategory(),
                    exam.getExamType(),
 
                    exam.getScheduledDate(),
                    exam.getScheduledTime(),
                    exam.getExamEndTime()
            );
        }
    }
}
 
 

 
 
// @Scheduled(cron = "0 * * * * *") // Every minute
@Scheduled(fixedRate = 5000)
public void generateEducatorProfileNotifications() {
 
    System.out.println("Checking educator profile updates...");
 
    LocalDateTime now = LocalDateTime.now();
    // LocalDateTime oneMinuteAgo = now.minusMinutes(1);
    LocalDateTime oneSecondAgo = now.minusSeconds(5);
 
    // 1️⃣ Find recently updated educators
    List<Educator> educators =
            educatorRepository.findRecentlyUpdatedEducators(
                    oneSecondAgo,
                    now
            );
    // 2️⃣ Create notifications
    for (Educator educator : educators) {
 
        notificationService.createEducatorProfileUpdateNotification(
 
                educator.getTenantId(),
                educator.getEducatorId()
 
               
        );
    }
    
}
 
// @Scheduled(cron = "0 * * * * *") // Every minute
@Scheduled(fixedRate = 5000)
public void generateAbortedScheduledExamNotifications() {
 
    System.out.println("Checking aborted exams...");
 
    LocalDateTime now = LocalDateTime.now();
    // LocalDateTime oneMinuteAgo = now.minusMinutes(1);
    LocalDateTime oneSecondAgo = now.minusSeconds(5);
 
    // ================= Mains =================
       
    List<ScheduledExam> aborted =
            studentRepo.findRecentlyAbortedExams(
                    ExamStatus.ABORTED,
                    oneSecondAgo,
                    now
            );
            //Optional<EducatorScheduledExam> examOpt= educatorRepo.findByEduScheduledExamId(abortedMains.get(0).getEduScheduledExamId());
 
 
    for (ScheduledExam exam : aborted) {
 
 
 
        notificationService.createScheduledExamAbortedNotification(
 
                exam.getTenantId(),
                exam.getUserId(),
                exam.getEducatorId(),
                exam.getExamType(),
                // Category.MAINS,
 
                exam.getEduScheduledExamId(),
                exam.getScheduledExamId(),
 
                exam.getBatchId(),
                exam.getBranchId()
                // examOpt.get().getScheduledDate(),
                // examOpt.get().getScheduledTime(),
                // examOpt.get().getEndTime()
        );
    }
 

}
 
 
// @Scheduled(cron = "0 * * * * *") // Every minute
@Scheduled(fixedRate = 5000)
public void generateAbortedExamNotifications() {
 
    System.out.println("Checking normal aborted exams...");
 
    LocalDateTime now = LocalDateTime.now();
    // LocalDateTime oneMinuteAgo = now.minusMinutes(1);
    LocalDateTime oneSecondAgo = now.minusSeconds(5);
 
    // ================= Mains =================
       
    List<Exam> abortedMains =
            examRepo.findRecentlyAbortedExams(
                Exam.ExamStatus.ABORTED,
                    oneSecondAgo,
                    now
            );
            //Optional<EducatorScheduledExam> examOpt= educatorRepo.findByEduScheduledExamId(abortedMains.get(0).getEduScheduledExamId());
 
 
    for (Exam exam : abortedMains) {
 
 
 
        notificationService.createExamAbortedNotification(
 
                exam.getTenantId(),
                exam.getUser().getUserId(),
               exam.getExamId(),
                exam.getExamType(),
                // Category.MAINS,
 
             
 
                exam.getTotalDuration().toString()
                // examOpt.get().getScheduledDate(),
                // examOpt.get().getScheduledTime(),
                // examOpt.get().getEndTime()
        );
    }
 
   
}


@Scheduled(fixedRate = 5000)
public void generateExamParticipationReport() {

    System.out.println("Checking completed exams for participation report...");

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime past = now.minusSeconds(5);

    List<EducatorScheduledExam> completedExams =
            educatorRepo.findCompletedExams(past,now); // create this

    for (EducatorScheduledExam exam : completedExams) {

        // 🔥 DUPLICATE CHECK (IMPORTANT)
        boolean alreadySent = notificationRepository
                .existsByTenantIdAndTitleAndCreatedAtAfter(
                        exam.getTenantId(),
                        "Exam Participation Report",
                        LocalDateTime.now().minusMinutes(5)
                );

        if (alreadySent) continue;

        Long eduScheduledExamId = exam.getEduScheduledExamId();

        int totalStudents = (int) studentRepo
                .countByEduScheduledExamId(eduScheduledExamId);

        int presentStudents = (int) studentRepo
                .countByEduScheduledExamIdAndStatusIn(
                        eduScheduledExamId,
                        List.of(
                                ScheduledExam.ExamStatus.COMPLETED,
                                ScheduledExam.ExamStatus.IN_PROGRESS,
                                ScheduledExam.ExamStatus.ABORTED
                        )
                );

        int absentStudents = totalStudents - presentStudents;

        // 🔹 FETCH STUDENTS
    List<StudentResultDTO> students =
    studentRepo.getAllStudentResults(eduScheduledExamId);

// 🔹 SORT BY SCORE (DESC)
students.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        List<StudentResultDTO> top3 =
        students.subList(0, Math.min(3, students.size()));

List<StudentResultDTO> low3 =
        students.subList(
                Math.max(students.size() - 3, 0),
                students.size()
        );

        notificationService.createExamParticipationNotificationForEducators(
                exam.getTenantId(),
                exam.getExamType().name(),
                eduScheduledExamId,
                // exam.getBranchId(),
                // exam.getBatchId(),
                totalStudents,
                presentStudents,
                absentStudents,
                top3,
                low3
                
        //          topPerformers,
        // lowPerformers

        );

        System.out.println("✅ Participation notification sent for exam: " + eduScheduledExamId);
    }
}
 
}
 
 
 