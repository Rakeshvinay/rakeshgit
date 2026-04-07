// // package com.brihathi.Multi_Tenant.service;
 
// // import java.time.LocalDateTime;
// // import java.util.List;
// // import java.util.Map;
 
// // import org.springframework.stereotype.Service;
// // import com.brihathi.Multi_Tenant.repository.ScheduledScorePredictorRepository;
// // import com.brihathi.Multi_Tenant.repository.ScheduledSubjectWisePerformanceRepository;
// // import com.brihathi.Multi_Tenant.repository.ScheduledYourScoreProgressRepository;
// // import com.brihathi.Multi_Tenant.repository.UserRepository;
 
// // import lombok.AllArgsConstructor;
 
// // import com.brihathi.Multi_Tenant.dto.PredictedRankDTO;
// // import com.brihathi.Multi_Tenant.dto.ScoreProgressResponseDTO;
// // import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
// // import com.brihathi.Multi_Tenant.dto.UserDTO;
// // import com.brihathi.Multi_Tenant.entity.User;
 
 
 
// // @Service
// // @AllArgsConstructor
// // public class EducatorDashboardIndvlStdPerService {
// //     private final ScheduledScorePredictorRepository scheduledScorePredictorRepository;
// //     private final ScheduledSubjectWisePerformanceRepository scheduledSubjectWisePerformanceRepository;
// //     private final ScheduledYourScoreProgressRepository scheduledYourScoreProgressRepository;
// //     private final UserRepository userRepository;
   
// //     public PredictedRankDTO getPredictedRank(Long userId) {
// //         return scheduledScorePredictorRepository.getPredictedRankForUser(userId);
// //     }
 
// //     public List<SubjectWisePerformanceDTO> getSubjectWisePerformance(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
// //         return scheduledSubjectWisePerformanceRepository.getSubjectWisePerformance(userId, startDate, endDate);
// //     }
 
// //     public List<ScoreProgressResponseDTO> getScoreProgress(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
// //         // Find user's first exam date
// //         java.sql.Timestamp firstExamTs = scheduledYourScoreProgressRepository.findFirstExamDate(userId);
// //         LocalDateTime firstExamDate = (firstExamTs != null) ? firstExamTs.toLocalDateTime() : startDate;
// //         // Use firstExamDate as the base for week calculation
// //         List<Object[]> rows = scheduledYourScoreProgressRepository.getScoreProgressByWeek(userId, firstExamDate, endDate);
// //         Map<String, List<ScoreProgressResponseDTO.WeekStat>> subjectToWeeks = new java.util.LinkedHashMap<>();
// //         for (Object[] row : rows) {
// //             String subject = (String) row[0];
// //             int week = ((Number) row[1]).intValue();
// //             Double avg = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
// //             Double total = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
// //             Integer count = row[4] != null ? ((Number) row[4]).intValue() : 0;
// //             subjectToWeeks
// //                 .computeIfAbsent(subject, k -> new java.util.ArrayList<>())
// //                 .add(new ScoreProgressResponseDTO.WeekStat(week, avg, total, count));
// //         }
// //         // Set diffFromPreviousWeek for each subject's weeks
// //         for (List<ScoreProgressResponseDTO.WeekStat> weeks : subjectToWeeks.values()) {
// //             Double prev = null;
// //             for (ScoreProgressResponseDTO.WeekStat ws : weeks) {
// //                 if (prev == null) {
// //                     ws.setDiffFromPreviousWeek(null);
// //                 } else {
// //                     ws.setDiffFromPreviousWeek(ws.getAveragePercentage() - prev);
// //                 }
// //                 prev = ws.getAveragePercentage();
// //             }
// //         }
// //         // Fetch subject-level summary (still use original startDate)
// //         List<Object[]> summaryRows = scheduledYourScoreProgressRepository.getScoreProgressSubjectSummary(userId, startDate, endDate);
// //         Map<String, ScoreProgressResponseDTO.Summary> subjectToSummary = new java.util.HashMap<>();
// //         for (Object[] row : summaryRows) {
// //             String subject = (String) row[0];
// //             Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
// //             Double total = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
// //             Integer count = row[3] != null ? ((Number) row[3]).intValue() : 0;
// //             subjectToSummary.put(subject, new ScoreProgressResponseDTO.Summary(avg, total, count));
// //         }
// //         List<ScoreProgressResponseDTO> result = subjectToWeeks.entrySet().stream()
// //             .map(e -> new ScoreProgressResponseDTO(e.getKey(), e.getValue(), subjectToSummary.get(e.getKey())))
// //             .collect(java.util.stream.Collectors.toList());
// //         return result;
// //     }
 
 
 
// //     public UserDTO getUserByEnrollmentIdOrName(String enrollmentId, String name) {
 
// //         User user;
 
// //         if (enrollmentId != null) {
// //             user = userRepository.findByEnrollmentId(enrollmentId)
// //                     .orElseThrow(() -> new RuntimeException("User not found with enrollmentId"));
// //         } else {
// //             user = userRepository.findByNameIgnoreCase(name)
// //                     .orElseThrow(() -> new RuntimeException("User not found with name"));
// //         }
 
// //         return mapToDTO(user);
// //     }
 
// //     private UserDTO mapToDTO(User user) {
// //         return UserDTO.builder()
// //                 .userId(user.getUserId())
// //                 .name(user.getName())
// //                 //.enrollmentId(user.getEnrollmentId())
// //                 //.phoneNumber(user.getPhoneNumber())
// //                 .grade(user.getGrade())
// //                 .state(user.getState())
// //                 .city(user.getCity())
// //                 .enrollmentId(user.getEnrollmentId())
// //                 .build();
// //     }
 
// // }
 
// package com.brihathi.Multi_Tenant.service;
 
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Map;
 
// import org.springframework.stereotype.Service;
// import com.brihathi.Multi_Tenant.repository.ScheduledScorePredictorRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledSubjectWisePerformanceRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledYourScoreProgressRepository;
 
// import lombok.AllArgsConstructor;
 
// import com.brihathi.Multi_Tenant.dto.PredictedRankDTO;
// import com.brihathi.Multi_Tenant.dto.ScoreProgressResponseDTO;
// import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
 
 
 
// @Service
// @AllArgsConstructor
// public class EducatorDashboardIndvlStdPerService {
//     private final ScheduledScorePredictorRepository scheduledScorePredictorRepository;
//     private final ScheduledSubjectWisePerformanceRepository scheduledSubjectWisePerformanceRepository;
//     private final ScheduledYourScoreProgressRepository scheduledYourScoreProgressRepository;
   
//     public PredictedRankDTO getPredictedRank(Long userId) {
//         return scheduledScorePredictorRepository.getPredictedRankForUser(userId);
//     }
 
//     public List<SubjectWisePerformanceDTO> getSubjectWisePerformance(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
//         return scheduledSubjectWisePerformanceRepository.getSubjectWisePerformance(userId, startDate, endDate);
//     }
 
//     public List<ScoreProgressResponseDTO> getScoreProgress(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
//         // Find user's first exam date
//         java.sql.Timestamp firstExamTs = scheduledYourScoreProgressRepository.findFirstExamDate(userId);
//         LocalDateTime firstExamDate = (firstExamTs != null) ? firstExamTs.toLocalDateTime() : startDate;
//         // Use firstExamDate as the base for week calculation
//         List<Object[]> rows = scheduledYourScoreProgressRepository.getScoreProgressByWeek(userId, firstExamDate, endDate);
//         Map<String, List<ScoreProgressResponseDTO.WeekStat>> subjectToWeeks = new java.util.LinkedHashMap<>();
//         for (Object[] row : rows) {
//             String subject = (String) row[0];
//             int week = ((Number) row[1]).intValue();
//             Double avg = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
//             Double total = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
//             Integer count = row[4] != null ? ((Number) row[4]).intValue() : 0;
//             subjectToWeeks
//                 .computeIfAbsent(subject, k -> new java.util.ArrayList<>())
//                 .add(new ScoreProgressResponseDTO.WeekStat(week, avg, total, count));
//         }
//         // Set diffFromPreviousWeek for each subject's weeks
//         for (List<ScoreProgressResponseDTO.WeekStat> weeks : subjectToWeeks.values()) {
//             Double prev = null;
//             for (ScoreProgressResponseDTO.WeekStat ws : weeks) {
//                 if (prev == null) {
//                     ws.setDiffFromPreviousWeek(null);
//                 } else {
//                     ws.setDiffFromPreviousWeek(ws.getAveragePercentage() - prev);
//                 }
//                 prev = ws.getAveragePercentage();
//             }
//         }
//         // Fetch subject-level summary (still use original startDate)
//         List<Object[]> summaryRows = scheduledYourScoreProgressRepository.getScoreProgressSubjectSummary(userId, startDate, endDate);
//         Map<String, ScoreProgressResponseDTO.Summary> subjectToSummary = new java.util.HashMap<>();
//         for (Object[] row : summaryRows) {
//             String subject = (String) row[0];
//             Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
//             Double total = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
//             Integer count = row[3] != null ? ((Number) row[3]).intValue() : 0;
//             subjectToSummary.put(subject, new ScoreProgressResponseDTO.Summary(avg, total, count));
//         }
//         List<ScoreProgressResponseDTO> result = subjectToWeeks.entrySet().stream()
//             .map(e -> new ScoreProgressResponseDTO(e.getKey(), e.getValue(), subjectToSummary.get(e.getKey())))
//             .collect(java.util.stream.Collectors.toList());
//         return result;
//     }
 
// }
 
 