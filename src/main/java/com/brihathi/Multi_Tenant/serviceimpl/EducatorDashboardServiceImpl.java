package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.dto.*;
import com.brihathi.Multi_Tenant.service.EducatorDashboardService;
import com.brihathi.Multi_Tenant.service.NotificationService;
import com.brihathi.Multi_Tenant.entity.Educator;
import java.sql.Timestamp;

import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.EducatorScheduledExam;
import com.brihathi.Multi_Tenant.entity.ScheduledExam;
import com.brihathi.Multi_Tenant.entity.ScheduledExamResultsSummary;
import com.brihathi.Multi_Tenant.entity.ScheduledScorePredictor;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.service.TenantService;
import com.brihathi.Multi_Tenant.repository.NotificationRepository;
import com.brihathi.Multi_Tenant.repository.BranchRepository;
import com.brihathi.Multi_Tenant.repository.BatchRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamResultRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledSubjectWisePerformanceBarRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamResultsSummaryRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledScorePredictorRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledYourScoreProgressRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamFinalResultRepository;
import com.brihathi.Multi_Tenant.repository.EducatorScheduledExamRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.QuestionPublicRepository;
import com.brihathi.Multi_Tenant.repository.QuestionTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;


@Service
@RequiredArgsConstructor
public class EducatorDashboardServiceImpl implements EducatorDashboardService {

    private final TenantService tenantService;
    private final NotificationService notificationService;
    private final BranchRepository branchRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final AuthenticationManager authenticationManager;
    private final QuestionPublicRepository questionPublicRepository;
    private final QuestionTenantRepository questionTenantRepository;
    private final ScheduledExamResultRepository scheduledExamResultRepository;
    private final ScheduledScorePredictorRepository scheduledScorePredictorRepository;
    private final ScheduledSubjectWisePerformanceBarRepository scheduledSubjectWisePerformanceBarRepository;
    private final ScheduledExamResultsSummaryRepository scheduledExamResultsSummaryRepository;
    private final EducatorScheduledExamRepository educatorScheduledExamRepository;
    private final ScheduledExamRepository scheduledExamRepository;
    private final ScheduledExamFinalResultRepository scheduledExamFinalResultRepository;
    private final ScheduledYourScoreProgressRepository scheduledYourScoreProgressRepository;
    private final TenantRepository tenantRepository;


//     public EducatorDashboardServiceImpl(AuthenticationManager authenticationManager){this.authenticationManager = authenticationManager;}

    @Override
    public TenantDashboardDTO getDashboardStructure(String subdomain) {

        Tenant tenant = tenantService.getTenantBySubdomain(subdomain);

        List<Object[]> branchStats = branchRepository.getBranchStats(tenant.getTenantId());
        List<BranchDashboardDTO> branches = new ArrayList<>();

        for (Object[] branch : branchStats) {

            String branchId = (String) branch[0];
            String branchName = (String) branch[1];
            Long totalBatches = (Long) branch[2];
            Long totalStudents = (Long) branch[3];

            List<Object[]> batchStats = batchRepository.getBatchStats(branchId);

            List<BatchDashboardDTO> batches = batchStats.stream()
                    .map(b -> new BatchDashboardDTO(
                            (String) b[0],
                            (String) b[1],
                            (Long) b[2]
                    )).toList();

                    List<Object[]> performanceData =
                    scheduledSubjectWisePerformanceBarRepository.getBranchSubjectPerformance(branchName,        // 👈 NOT branchId
                        tenant.getTenantId());

List<BranchSubjectPerformanceDTO> subjectPerformance =
        performanceData.stream()
                .map(p -> new BranchSubjectPerformanceDTO(
                        (String) p[0],
                        ((Number) p[1]).doubleValue(),
                        ((Number) p[2]).longValue(),
                        ((Number) p[3]).doubleValue()
                )).toList();
                Double branchAverage = 0.0;

                if (!subjectPerformance.isEmpty()) {
                    branchAverage = subjectPerformance.stream()
                            .mapToDouble(BranchSubjectPerformanceDTO::getAveragePercentage)
                            .average()
                            .orElse(0.0);
                }
                

            branches.add(new BranchDashboardDTO(
                    branchId, branchName, totalBatches, totalStudents, batches, subjectPerformance , branchAverage 
            ));
        }

        return new TenantDashboardDTO(tenant.getSubdomain(), branches);
    }




//     @Override
// public ExamResultsDashboardDTO getExamResults(Long eduExamId) {

//         EducatorScheduledExam exam = educatorScheduledExamRepository
//         .findById(eduExamId)
//         .orElseThrow(() -> new RuntimeException("Exam not found"));


//     List<StudentResultDTO> students =
//     scheduledExamResultsSummaryRepository.getStudentResults(eduExamId);

//     // 🔹 Set Rank
//     for (int i = 0; i < students.size(); i++) {
//         students.get(i).setRank(i + 1);
//     }

//     List<Object[]> statsList = scheduledExamResultsSummaryRepository.getExamSummary(eduExamId);

//     Object[] stats = statsList.isEmpty() ? new Object[]{0,0,0,0} : statsList.get(0);
    
//     Double avg = stats[0] == null ? 0.0 : ((Number) stats[0]).doubleValue();
//     Integer high = stats[1] == null ? 0 : ((Number) stats[1]).intValue();
//     Long total = stats[2] == null ? 0L : ((Number) stats[2]).longValue();
//     Long passed = stats[3] == null ? 0L : ((Number) stats[3]).longValue();
    
//     Double passPercent = total == 0 ? 0 :
//             (passed * 100.0) / total;
    
//             String subject = exam.getExamType().name();
//             String branch = exam.getBranch();
//             String batch = exam.getBatch();
            
//     return ExamResultsDashboardDTO.builder()
//             .averageScore(avg)
//             .highestScore(high)
//             .passPercentage(passPercent)
//             .totalStudents(total.intValue())
//             .maxMarks(100) // or from exam config
//             .studentResults(students)
//             .build();
// }

// @Override
// public ExamResultsDashboardDTO getExamResults(Long eduExamId) {

//     EducatorScheduledExam exam = educatorScheduledExamRepository
//             .findByEduScheduledExamId(eduExamId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     String subject = exam.getExamType().name();
//     String branch = exam.getBranch();
//     String batch = exam.getBatch();

//     int maxMarks = exam.getExamType() == Subject.ALL ? 720 : 180;


//     List<StudentResultDTO> students =
//         scheduledExamRepository.getAllStudentResults(eduExamId);

// // 🔹 Ranking based only on score
// students.sort((a, b) -> b.getScore().compareTo(a.getScore()));

// for (int i = 0; i < students.size(); i++) {
//     students.get(i).setRank(i + 1);

//     double percent = (students.get(i).getScore() * 100.0) / maxMarks;
//     students.get(i).setPercentage(percent);
// }

// List<Object[]> attList = scheduledExamRepository.getAttendanceStats(eduExamId);

// Object[] attendance = attList.isEmpty() ? new Object[]{0,0} : attList.get(0);

// int absent = attendance[0] == null ? 0 : ((Number) attendance[0]).intValue();
// int present = attendance[1] == null ? 0 : ((Number) attendance[1]).intValue();


// //     List<StudentResultDTO> students =
// //             scheduledExamResultsSummaryRepository.getStudentResults(eduExamId);

//     // 🔹 Fix percentage using maxMarks
//     for (StudentResultDTO s : students) {
//         double percent = (s.getScore() * 100.0) / maxMarks;
//         s.setPercentage(percent);
//     }

//     // 🔹 Ranking
//     for (int i = 0; i < students.size(); i++) {
//         students.get(i).setRank(i + 1);
//     }

//     List<Object[]> statsList = scheduledExamResultsSummaryRepository.getExamSummary(eduExamId);
//     Object[] stats = statsList.isEmpty() ? new Object[]{0,0,0,0} : statsList.get(0);

//     Double avg = ((Number) stats[0]).doubleValue();
//     Integer high = ((Number) stats[1]).intValue();
//     Long total = ((Number) stats[2]).longValue();
//     Long passed = ((Number) stats[3]).longValue();

//     Double passPercent = total == 0 ? 0 :
//             (passed * 100.0) / total;

//             return ExamResultsDashboardDTO.builder()
//             .subject(subject)
//             .branch(branch)
//             .batch(batch)
//             .maxMarks(maxMarks)
//             .averageScore(avg)
//             .highestScore(high)
//             .passPercentage(passPercent)
//             .totalStudents(total.intValue())
//             .presentStudents(present)
//             .absentStudents(absent)
//             .studentResults(students)
//             .build();
    
// }


@Override
public ExamResultsDashboardDTO getExamResults(Long eduExamId) {

    EducatorScheduledExam exam = educatorScheduledExamRepository
            .findByEduScheduledExamId(eduExamId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    String subject = exam.getExamType().name();
    String branch = exam.getBranch();
    String batch = exam.getBatch();

    int maxMarks = exam.getExamType() == Subject.ALL ? 720 : 180;

    // 🔹 FETCH STUDENTS
    List<StudentResultDTO> students =
            scheduledExamRepository.getAllStudentResults(eduExamId);

    // 🔹 SORT BY SCORE (DESC)
    students.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
    List<StudentResultDTO> top3 =
        students.subList(0, Math.min(3, students.size()));

List<StudentResultDTO> low3 =
        students.subList(
                Math.max(students.size() - 3, 0),
                students.size()
        );

    // 🔹 SET RANK + % + scheduledExamId
    for (int i = 0; i < students.size(); i++) {
        StudentResultDTO s = students.get(i);

        s.setRank(i + 1);

        double percent = maxMarks == 0 ? 0 :
                (s.getScore() * 100.0) / maxMarks;
        s.setPercentage(percent);

        // ✅ ADD THIS
        if (s.getScheduledExamId() == null) {
            s.setScheduledExamId(
                    scheduledExamRepository
                        .findScheduledExamIdByEduExamAndUser(
                                eduExamId, s.getRollNo())
            );
        }
    }

    // 🔹 ATTENDANCE
    List<Object[]> attList = scheduledExamRepository.getAttendanceStats(eduExamId);
    Object[] attendance = attList.isEmpty() ? new Object[]{0,0} : attList.get(0);

    int absent = attendance[0] == null ? 0 : ((Number) attendance[0]).intValue();
    int present = attendance[1] == null ? 0 : ((Number) attendance[1]).intValue();

    // 🔹 SUMMARY STATS (SAFE)
    List<Object[]> statsList = scheduledExamResultsSummaryRepository.getExamSummary(eduExamId);
    Object[] stats = statsList.isEmpty() ? new Object[]{0,0,0,0} : statsList.get(0);

    Double avg = stats[0] == null ? 0.0 : ((Number) stats[0]).doubleValue();
    Integer high = stats[1] == null ? 0 : ((Number) stats[1]).intValue();
    Long total = stats[2] == null ? 0L : ((Number) stats[2]).longValue();
    Long passed = stats[3] == null ? 0L : ((Number) stats[3]).longValue();

    Double passPercent = total == 0 ? 0 :
            (passed * 100.0) / total;

    return ExamResultsDashboardDTO.builder()
            .subject(subject)
            .branch(branch)
            .batch(batch)
            .maxMarks(maxMarks)
            .averageScore(avg)
            .highestScore(high)
            .passPercentage(passPercent)
            .totalStudents(total.intValue())
            .presentStudents(present)
            .absentStudents(absent)
            .studentResults(students)
            .build();
}


        @Override
        public StudentResultDetailsDTO getStudentResultDetails(Long scheduledExamId, String subdomain) {
        
            Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));
        
            ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
                    .orElseThrow(() -> new RuntimeException("Exam not found"));
        
            EducatorScheduledExam eduExam =
                    educatorScheduledExamRepository.findById(exam.getEduScheduledExamId())
                    .orElseThrow(() -> new RuntimeException("Educator exam not found"));
        
            ScheduledExamResultsSummary summary =
                    scheduledExamResultsSummaryRepository.findByScheduledExamId(scheduledExamId)
                    .orElseThrow(() -> new RuntimeException("Result summary not found"));
        
            User student = userRepository.findById(exam.getUserId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
        

//     User user = userRepository.findById(userId).orElseThrow();

//     ScheduledExamResultsSummary summary =
//             scheduledExamResultsSummaryRepository.findByScheduledExamId(scheduledExam.getScheduledExamId())
//             .orElseThrow(() -> new RuntimeException("Result not found"));

    // 🔹 SCORE
    int score = summary.getCorrectedCount() * 4 - summary.getWrongCount();
    int maxMarks = exam.getExamType() == Subject.ALL ? 720 : 180;
    double percentage = score * 100.0 / maxMarks;

    // 🔹 ANALYTICS
    ResultAnalyticsDTO analytics = new ResultAnalyticsDTO(
            summary.getCorrectedCount(),
            summary.getWrongCount(),
            summary.getNotAnswered()
    );
    boolean usePublicQuestions =
    "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());
    // 🔹 FETCH ALL QUESTIONS OF EXAM
    List<QuestionReviewDTO> questions =
            usePublicQuestions
                    ? scheduledExamResultRepository.getReviewFromPublic(exam.getScheduledExamId())
                    : scheduledExamResultRepository.getReviewFromTenant(exam.getScheduledExamId());

    return StudentResultDetailsDTO.builder()
    .rank(0) 
            .studentName(student.getName())
            .rollNo(student.getEnrollmentId())
            .branch(student.getBranch())
            .batch(student.getBatch())
            .subject(exam.getExamType().name())
            .maxMarks(maxMarks)
            .score(score)
            .percentage(percentage)
            .studentAttendance(
                    exam.getStatus() == ScheduledExam.ExamStatus.UNATTEMPTED
                            ? "ABSENT" : "PRESENT"
            )
            .analytics(analytics)
            .questions(questions)
            .build();

        //     notificationService.createExamParticipationNotificationForEducators(
        //         tenantId,
        //         report.getSubject(),
        //         report.getBranch(),
        //         report.getBatch(),
        //         report.getTotalStudents(),
        //         report.getPresentStudents(),
        //         report.getAbsentStudents()
        // );
}
// @Override
// public StudentResultDetailsDTO getStudentResultDetails(Long scheduledExamId, String subdomain) {


//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     EducatorScheduledExam eduExam =
//             educatorScheduledExamRepository.findById(exam.getEduScheduledExamId())
//             .orElseThrow(() -> new RuntimeException("Educator exam not found"));

//     ScheduledExamResultsSummary summary =
//             scheduledExamResultsSummaryRepository.findByScheduledExamId(scheduledExamId)
//             .orElseThrow(() -> new RuntimeException("Result summary not found"));

//     User student = userRepository.findById(exam.getUserId())
//             .orElseThrow(() -> new RuntimeException("Student not found"));

//     // 🔹 SCORE
//     int score = summary.getCorrectedCount() * 4 - summary.getWrongCount();
//     int maxMarks = exam.getExamType() == Subject.ALL ? 720 : 180;
//     double percentage = score * 100.0 / maxMarks;

//     // 🔹 ANALYTICS
//     ResultAnalyticsDTO analytics = new ResultAnalyticsDTO(
//             summary.getCorrectedCount(),
//             summary.getWrongCount(),
//             summary.getNotAnswered()
//     );

//     boolean usePublicQuestions =
//             "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

//     // 🔹 QUESTIONS
//     List<QuestionReviewDTO> questions =
//             usePublicQuestions
//                     ? scheduledExamResultRepository.getReviewFromPublic(exam.getScheduledExamId())
//                     : scheduledExamResultRepository.getReviewFromTenant(exam.getScheduledExamId());

//    // ============================================================
// // 🔥 EDUCATOR NOTIFICATION (RUN ONLY ONCE)
// // ============================================================

// String title = "Exam Participation Report";

// boolean alreadySent = notificationRepository
//         .existsByTenantIdAndTitleAndCreatedAtAfter(
//                 tenant.getTenantId(),
//                 title,
//                 LocalDateTime.now().minusMinutes(5)
//         );

// if (!alreadySent) {

//     Long eduScheduledExamId = exam.getEduScheduledExamId();

//     // 🔹 TOTAL STUDENTS
//     int totalStudents = (int) scheduledExamRepository
//             .countByEduScheduledExamId(eduScheduledExamId);

//     // 🔹 PRESENT STUDENTS
//     List<ScheduledExam.ExamStatus> presentStatuses = List.of(
//             ScheduledExam.ExamStatus.COMPLETED,
//             ScheduledExam.ExamStatus.IN_PROGRESS,
//             ScheduledExam.ExamStatus.ABORTED
//     );

//     int presentStudents = (int) scheduledExamRepository
//             .countByEduScheduledExamIdAndStatusIn(
//                     eduScheduledExamId,
//                     presentStatuses
//             );

//     // 🔹 ABSENT
//     int absentStudents = totalStudents - presentStudents;

//     notificationService.createExamParticipationNotificationForEducators(
//             tenant.getTenantId(),
//             exam.getExamType().name(),
//             student.getBranch(),
//             student.getBatch(),
//             totalStudents,
//             presentStudents,
//             absentStudents
//     );

//     System.out.println("✅ Exam Participation Notification Triggered");
// }

//     // ============================================================

//     return StudentResultDetailsDTO.builder()
//             .rank(0)
//             .studentName(student.getName())
//             .rollNo(student.getEnrollmentId())
//             .branch(student.getBranch())
//             .batch(student.getBatch())
//             .subject(exam.getExamType().name())
//             .maxMarks(maxMarks)
//             .score(score)
//             .percentage(percentage)
//             .studentAttendance(
//                     exam.getStatus() == ScheduledExam.ExamStatus.UNATTEMPTED
//                             ? "ABSENT" : "PRESENT"
//             )
//             .analytics(analytics)
//             .questions(questions)
//             .build();
// }
@Override
public List<StudentSearchDTO> getStudentsForSearch(String subdomain, String search) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    Object principal = auth.getPrincipal();

    if (!(principal instanceof Educator educator)) {
        throw new RuntimeException("Educator not authorized");
    }

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    return userRepository.searchStudents(tenant.getTenantId(), search);
}



@Override
public StudentExamStatsDTO getStudentExamStats(Long userId, String subdomain) {

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    Long tenantId = tenant.getTenantId();

    // 🔴 STEP 1 — CHECK USER EXISTS
    User user = userRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + userId));

    // 🔹 STEP 2 — COUNT EXAMS
    Long attempted = scheduledExamRepository.countAttempted(userId, tenantId);
    Long unattempted = scheduledExamRepository.countUnattempted(userId, tenantId);

    // 🔹 STEP 3 — PREDICTOR DATA
    List<ScheduledScorePredictor> predictorList =
            scheduledScorePredictorRepository.findLatest(userId, tenantId);

    Double predictedScore = 0.0;
    Integer predictedRank = 0;

    if (!predictorList.isEmpty()) {
        ScheduledScorePredictor predictor = predictorList.get(0);
        predictedScore = predictor.getPredictedScore();
        predictedRank = predictor.getPredictedRank();
    }

    return StudentExamStatsDTO.builder()
            .attemptedExams(attempted)
            .unattemptedExams(unattempted)
            .predictedScore(predictedScore)
            .predictedRank(predictedRank)
            .build();
}




@Override
public List<StudentSubjectPerformanceDTO> getSubjectWisePerformance(Long userId, String subdomain) {

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    Long tenantId = tenant.getTenantId();

    // 🔴 Validate student exists
    userRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

    List<Object[]> rows =
            scheduledSubjectWisePerformanceBarRepository
                    .getStudentSubjectPerformance(userId, tenantId);

    List<StudentSubjectPerformanceDTO> response = new ArrayList<>();

    for (Object[] r : rows) {

        Double total = ((Number) r[0]).doubleValue();
        Long count = ((Number) r[1]).longValue();
        Double avg = ((Number) r[2]).doubleValue();
        String subject = (String) r[3];

        String desc;

        if (avg >= 90)
            desc = "Excellent grasp of concepts";
        else if (avg >= 70)
            desc = "Good, can be pushed higher";
        else if (avg >= 50)
            desc = "Needs improvement";
        else
            desc = "Weak area, focus required";

        response.add(
                StudentSubjectPerformanceDTO.builder()
                        .totalPercentage(total)
                        .countRows(count)
                        .averagePercentage(avg)
                        .subject(subject)
                        .description(desc)
                        .build()
        );
    }

    return response;
}



// @Override
// public List<ScoreProgressDTO> getScoreProgress(Long userId, String subdomain) {

//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     Long tenantId = tenant.getTenantId();

//     // 🔴 Validate student exists
//     userRepository.findByUserIdAndTenantId(userId, tenantId)
//             .orElseThrow(() -> new RuntimeException("Student not found"));

//     List<Object[]> rows =
//             scheduledExamFinalResultRepository.getScoreTrend(userId, tenantId);

//     List<ScoreProgressDTO> response = new ArrayList<>();

//     int count = 1;

//     for (Object[] r : rows) {
//         Integer score = (Integer) r[0];
//         ZonedDateTime date = (ZonedDateTime) r[1];

//         response.add(
//                 ScoreProgressDTO.builder()
//                         .testName("Test " + count++)
//                         .score(score)
//                         .examDate(date)
//                         .build()
//         );
//     }

//     return response;
// }
@Override
public List<ScoreProgressResponseDTO> getScoreProgress(Long userId, String subdomain) {

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    userRepository.findByUserIdAndTenantId(userId, tenant.getTenantId())
            .orElseThrow(() -> new RuntimeException("Student not found"));

    LocalDateTime endDate = LocalDateTime.now();

    Timestamp firstExamTs = scheduledYourScoreProgressRepository.findFirstExamDate(userId);
    LocalDateTime startDate = firstExamTs != null
            ? firstExamTs.toLocalDateTime()
            : endDate.minusWeeks(1);

    List<Object[]> rows = scheduledYourScoreProgressRepository
            .getScoreProgressByWeek(userId, startDate, endDate);

    Map<String, List<ScoreProgressResponseDTO.WeekStat>> subjectToWeeks = new LinkedHashMap<>();

    for (Object[] row : rows) {
        String subject = (String) row[0];
        int week = ((Number) row[1]).intValue();
        Double avg = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
        Double total = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
        Integer count = row[4] != null ? ((Number) row[4]).intValue() : 0;

        subjectToWeeks
                .computeIfAbsent(subject, k -> new ArrayList<>())
                .add(new ScoreProgressResponseDTO.WeekStat(week, avg, total, count));
    }

    // 🔥 Week-to-week difference
    for (List<ScoreProgressResponseDTO.WeekStat> weeks : subjectToWeeks.values()) {
        Double prev = null;
        for (ScoreProgressResponseDTO.WeekStat ws : weeks) {
            ws.setDiffFromPreviousWeek(prev == null ? null : ws.getAveragePercentage() - prev);
            prev = ws.getAveragePercentage();
        }
    }

    // 🔹 Subject summary
    List<Object[]> summaryRows =
            scheduledYourScoreProgressRepository.getScoreProgressSubjectSummary(userId, startDate, endDate);

    Map<String, ScoreProgressResponseDTO.Summary> subjectToSummary = new HashMap<>();

    for (Object[] row : summaryRows) {
        subjectToSummary.put(
                (String) row[0],
                new ScoreProgressResponseDTO.Summary(
                        ((Number) row[1]).doubleValue(),
                        ((Number) row[2]).doubleValue(),
                        ((Number) row[3]).intValue()
                )
        );
    }

    return subjectToWeeks.entrySet().stream()
            .map(e -> new ScoreProgressResponseDTO(
                    e.getKey(),
                    e.getValue(),
                    subjectToSummary.get(e.getKey())))
            .toList();
}

}
