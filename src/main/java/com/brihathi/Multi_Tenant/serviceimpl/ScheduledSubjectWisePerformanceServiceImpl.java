// package com.brihathi.Multi_Tenant.serviceimpl;
 
// import com.brihathi.Multi_Tenant.dto.ChapterWisePerformanceDTO;
// import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
// import com.brihathi.Multi_Tenant.entity.*;
// import com.brihathi.Multi_Tenant.repository.*;
// import com.brihathi.Multi_Tenant.service.ScheduledSubjectWisePerformanceService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
 
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.stream.Collectors;
 
// @Service
// public class ScheduledSubjectWisePerformanceServiceImpl implements ScheduledSubjectWisePerformanceService {
 
//     @Autowired
//     private UserRepository userRepository;
 
//     @Autowired
//     private ScheduledExamRepository scheduledExamRepository;
 
//     @Autowired
//     private ScheduledSubjectWisePerformanceBarRepository subjectWisePerformanceBarRepository;
 
//     @Autowired
//     private  ScheduledExamResultRepository scheduledExamResultRepository ;
 
//     @Autowired
//     private ScheduledChapterWiseResultRepository scheduledChapterWiseResultRepository;
 
//     @Override
//     public void aggregateAndInsertSubjectWisePerformance(Long eduScheduledExamId, Long userId) {
//         User user = userRepository.findById(userId).orElseThrow();
//         ScheduledExam exam = scheduledExamRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId).orElseThrow();
//         List<ScheduledExamResult> results = scheduledExamResultRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId);
 
//         Map<String, List<ScheduledExamResult>> bySubject = results.stream()
//             .collect(Collectors.groupingBy(ScheduledExamResult::getSubject));
 
//         for (Map.Entry<String, List<ScheduledExamResult>> entry : bySubject.entrySet()) {
//             String subject = entry.getKey();
//             List<ScheduledExamResult> subjectResults = entry.getValue();
 
//             int totalMarks = subjectResults.stream()
//                 .mapToInt(er -> {
//                     if ("correct".equals(er.getValidateAnswer())) return 4;
//                     if ("wrong".equals(er.getValidateAnswer())) return -1;
//                     return 0;
//                 }).sum();
 
//             int totalPossibleMarks = subjectResults.size() * 4;
//             BigDecimal percentage = totalPossibleMarks > 0 ?
//                     BigDecimal.valueOf((double) totalMarks / totalPossibleMarks * 100) :
//                     BigDecimal.ZERO;
 
//             percentage = percentage.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : percentage;
 
//             ScheduledSubjectWisePerformanceBar performanceBar = ScheduledSubjectWisePerformanceBar.builder()
//                 .uuid(UUID.randomUUID())
//                 .userId(userId)
//                 .eduScheduledExamId(eduScheduledExamId)
//                 .tenantId(exam.getTenantId())
//                 .scheduledExamId(exam.getScheduledExamId())
//                 .educatorId(exam.getEducatorId())
//                 .branchId(Long.parseLong(exam.getBranchId()))
//                 .batchId(Long.parseLong(exam.getBatchId()))
//                 .difficultyLevel(exam.getDifficulty() != null ? exam.getDifficulty().name() : null)
//                 .percentage(percentage)
//                 .subject(subject)
//                 .build();
 
//             subjectWisePerformanceBarRepository.save(performanceBar);
//         }
 
//         System.out.println("SubjectWisePerformanceBar created for userId=" + userId + ", examId=" + eduScheduledExamId);
//     }
 
//     @Override
//     public Map<String, Object> getIndividualSubjectwiseAnalytics(Long userId,  String subject, LocalDateTime startDate, LocalDateTime endDate) {
//         List<SubjectWisePerformanceDTO> subjectStats = subjectWisePerformanceBarRepository
//             .getSubjectWisePerformanceByUser(userId,startDate, endDate);
 
//         List<Map<String, Object>> bars = subjectStats.stream()
//             .map(dto -> {
//                 Map<String, Object> map = new HashMap<>();
//                 map.put("subject", dto.getSubject());
//                 map.put("percentage", dto.getAveragePercentage() != null ? Math.round(dto.getAveragePercentage()) : 0);
//                 return map;
//             })
//             .collect(Collectors.toList());
 
//         List<Map<String, Object>> aiInsights = subjectStats.stream()
//             .map(dto -> {
//                 Map<String, Object> insight = new HashMap<>();
//                 String subj = dto.getSubject();
//                 int percent = dto.getAveragePercentage() != null ? (int) Math.round(dto.getAveragePercentage()) : 0;
//                 String performance;
//                 if (percent >= 80) performance = "Well";
//                 else if (percent >= 70) performance = "Good";
//                 else if (percent >= 60) performance = "Average";
//                 else performance = "Needs Improvement";
//                 insight.put("subject", subj);
//                 insight.put("performance", performance);
 
//                 List<String> weakChapters = new ArrayList<>();
//                 try {
//                     List<ScheduledChapterWiseResult> chapterResults = scheduledChapterWiseResultRepository
//                         .findByUserIdAndSubject(userId, subj);
//                     chapterResults = chapterResults.stream()
//                         .filter(cr -> cr.getCreatedAt() != null
//                                 && !cr.getCreatedAt().isBefore(startDate)
//                                 && !cr.getCreatedAt().isAfter(endDate))
//                         .filter(cr -> cr.getChapter() != null)
//                         .collect(Collectors.toList());
 
//                     Map<String, Double> chapterAverages = chapterResults.stream()
//                         .collect(Collectors.groupingBy(
//                             ScheduledChapterWiseResult::getChapter,
//                             Collectors.averagingDouble(cr -> cr.getPercentage() != null ? cr.getPercentage().doubleValue() : 0.0)
//                         ));
 
//                     weakChapters = chapterAverages.entrySet().stream()
//                         .sorted(Map.Entry.comparingByValue())
//                         .limit(3)
//                         .map(Map.Entry::getKey)
//                         .collect(Collectors.toList());
 
//                 } catch (Exception ex) {
//                     // Log or fallback
//                 }
 
//                 insight.put("needToImprove", weakChapters);
//                 return insight;
//             })
//             .collect(Collectors.toList());
 
//         List<String> details = new ArrayList<>();
//         if (subject != null) {
//             try {
//                 List<ScheduledChapterWiseResult> chapterResults = scheduledChapterWiseResultRepository
//                     .findByUserIdAndSubject(userId, subject);
//                 chapterResults = chapterResults.stream()
//                     .filter(cr -> cr.getCreatedAt() != null
//                             && !cr.getCreatedAt().isBefore(startDate)
//                             && !cr.getCreatedAt().isAfter(endDate))
//                     .filter(cr -> cr.getChapter() != null)
//                     .collect(Collectors.toList());
 
//                 Map<String, Double> chapterAverages = chapterResults.stream()
//                     .collect(Collectors.groupingBy(
//                         ScheduledChapterWiseResult::getChapter,
//                         Collectors.averagingDouble(cr -> cr.getPercentage() != null ? cr.getPercentage().doubleValue() : 0.0)
//                     ));
 
//                 details = chapterAverages.entrySet().stream()
//                     .sorted(Map.Entry.comparingByValue())
//                     .limit(3)
//                     .map(Map.Entry::getKey)
//                     .collect(Collectors.toList());
 
//             } catch (Exception ex) {
//                 // fallback
//             }
//         }
 
//         Map<String, Object> result = new HashMap<>();
//         result.put("bars", bars);
//         result.put("aiInsights", aiInsights);
//         result.put("details", details);
//         return result;
//     }
 
//     @Override
//     public Map<String, Object> getIndividualChapterwiseAnalytics(Long userId, String subject, LocalDateTime startDate, LocalDateTime endDate) {
//         List<ChapterWisePerformanceDTO> chapterStats =
//             subjectWisePerformanceBarRepository.findChapterWisePerformanceByUserAndDifficultyAndSubject(
//                 userId, subject, startDate, endDate
//             );
 
//         List<Map<String, Object>> bars = chapterStats.stream()
//             .map(dto -> {
//                 Map<String, Object> map = new HashMap<>();
//                 map.put("chapter", dto.getChapter());
//                 map.put("percentage", dto.getAveragePercentage() != null ? Math.round(dto.getAveragePercentage()) : 0);
//                 return map;
//             })
//             .collect(Collectors.toList());
 
//         Map<String, Object> result = new HashMap<>();
//         result.put("bars", bars);
//         return result;
//     }
// }
package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.ScheduledSubjectWisePerformanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.math.RoundingMode; 


@Service
public class ScheduledSubjectWisePerformanceServiceImpl
        implements ScheduledSubjectWisePerformanceService {

    @Autowired
    private ScheduledExamRepository scheduledExamRepository;

    @Autowired
    private ScheduledExamResultRepository scheduledExamResultRepository;

    @Autowired
    private ScheduledSubjectWisePerformanceBarRepository subjectWiseRepo;

    /* =====================================================
       SUBJECT WISE PERFORMANCE BAR (SCHEDULED)
       ===================================================== */

       @Override
       @Transactional
       public void aggregateAndInsertSubjectWisePerformance(Long eduScheduledExamId, Long userId) {
       
           // 🔥 ALWAYS LOAD STUDENT ATTEMPT EXAM
           ScheduledExam scheduledExam =
                   scheduledExamRepository
                           .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
                           .orElse(null);
       
           if (scheduledExam == null) return;
       
           Long scheduledExamId = scheduledExam.getScheduledExamId();
       
           // 🔥 Results must also use STUDENT EXAM
           List<ScheduledExamResult> results =
                   scheduledExamResultRepository
                           .findByScheduledExamIdAndUserId(scheduledExamId, userId);
       
           if (results.isEmpty()) return;
       
           Map<String, List<ScheduledExamResult>> bySubject =
                   results.stream()
                           .collect(Collectors.groupingBy(ScheduledExamResult::getSubject));
       
           for (Map.Entry<String, List<ScheduledExamResult>> entry : bySubject.entrySet()) {
       
               String subject = entry.getKey();
               List<ScheduledExamResult> subjectResults = entry.getValue();
       
               // Idempotency
               boolean exists =
                       subjectWiseRepo
                               .findByScheduledExamIdAndUserIdAndSubject(
                                       scheduledExamId, userId, subject
                               ).isPresent();
       
               if (exists) continue;
       
               int totalMarks = subjectResults.stream()
                       .mapToInt(r -> {
                           if ("correct".equals(r.getValidateAnswer())) return 4;
                           if ("wrong".equals(r.getValidateAnswer())) return -1;
                           return 0;
                       })
                       .sum();
       
               int totalPossibleMarks = subjectResults.size() * 4;
       
               BigDecimal percentage =
                       totalPossibleMarks > 0
                               ? BigDecimal.valueOf(totalMarks)
                               .multiply(BigDecimal.valueOf(100))
                               .divide(BigDecimal.valueOf(totalPossibleMarks), 2, RoundingMode.HALF_UP)
                               : BigDecimal.ZERO;
       
               if (percentage.compareTo(BigDecimal.ZERO) < 0) {
                   percentage = BigDecimal.ZERO;
               }
       
               ScheduledSubjectWisePerformanceBar bar =
                       ScheduledSubjectWisePerformanceBar.builder()
                               .uuid(UUID.randomUUID())
                               
                               .scheduledExamId(scheduledExamId)
                               .eduScheduledExamId(eduScheduledExamId)
                               .userId(userId)
                               .tenantId(scheduledExam.getTenantId())
                               .educatorId(scheduledExam.getEducatorId())
                               .branchId(scheduledExam.getBranchId())
                               .batchId(scheduledExam.getBatchId())
                               .subject(subject)
                               .percentage(percentage)
                               .difficultyLevel(
                                       scheduledExam.getDifficulty() != null
                                               ? scheduledExam.getDifficulty().name()
                                               : null
                               )
                               .build();
       
               subjectWiseRepo.save(bar);
           }
       }
       
       
}
