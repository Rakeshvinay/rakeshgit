// package com.brihathi.Multi_Tenant.serviceimpl;
 
// import com.brihathi.Multi_Tenant.entity.ScheduledExamResultsSummary;
// import com.brihathi.Multi_Tenant.entity.ScheduledExam;
// import com.brihathi.Multi_Tenant.entity.User;
// import com.brihathi.Multi_Tenant.entity.ScheduledExamResult;
// import com.brihathi.Multi_Tenant.entity.ScheduledChapterResult;
// import com.brihathi.Multi_Tenant.entity.ScheduledChapterWiseResult;
// import com.brihathi.Multi_Tenant.entity.Chapter;
 
// import com.brihathi.Multi_Tenant.repository.ChapterRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledExamResultsSummaryRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
// import com.brihathi.Multi_Tenant.repository.UserRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledExamResultRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledChapterWiseResultRepository;
 
// import com.brihathi.Multi_Tenant.service.ScheduledExamResultSummaryService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
 
// import java.util.List;
// import java.util.Optional;
// import java.math.BigDecimal;
// import java.time.Duration;
// import java.util.UUID;
// import java.util.Map;
// import java.lang.StringBuilder;
// import com.brihathi.Multi_Tenant.service.ScheduledDifficultyWisePerformanceService;

// @Service
// public class ScheduledExamResultSummaryServiceImpl implements ScheduledExamResultSummaryService {
//     @Autowired
//     private ScheduledExamResultsSummaryRepository scheduledExamResultsSummaryRepository;
//     @Autowired
//     private ScheduledExamRepository scheduledExamRepository;
//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private ScheduledExamResultRepository examResultRepository;
//     @Autowired
//     private ScheduledChapterWiseResultRepository scheduledChapterWiseResultRepository;
//     @Autowired
//     private ChapterRepository chapterRepository;
//     @Autowired
//     private ScheduledDifficultyWisePerformanceService scheduledDifficultyWisePerformanceService;

//     @Override
//     @Transactional
//     public void createExamResultSummary(Long eduScheduledExamId, Long userId) {
//         // Idempotency: check if summary already exists
//         if (scheduledExamResultsSummaryRepository.findByUserIdAndEduScheduledExamId(userId, eduScheduledExamId).isPresent()) {
//             // System.out.println("ExamResultsSummary already exists for userId=" + userId + ", examId=" + examId);
//             return;
//         }
 
//         // Fetch User and Exam
//         Optional<User> userOpt = userRepository.findById(userId);
//         Optional<ScheduledExam> examOpt = scheduledExamRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId);
//         if (userOpt.isEmpty() || examOpt.isEmpty()) {
//             // System.out.println("User or Exam not found for summary generation");
//             return;
//         }
//         // User user = userOpt.get();
//         // ScheduledExam exam = examOpt.get();
 
//         // Fetch exam results ONLY from DB
//         List<ScheduledExamResult> examResults = examResultRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId);
//         if (examResults == null || examResults.isEmpty()) {
//             // System.out.println("No exam results found in DB for summary generation");
//             return;
//         }
 
//         // Aggregate statistics
//         int totalQuestions = examResults.size();
//         int totalAnswered = (int) examResults.stream().filter(ScheduledExamResult::getAnswered).count();
//         int notAnswered = totalQuestions - totalAnswered;
//         int markedForReview = (int) examResults.stream().filter(ScheduledExamResult::getMarkedForReview).count();
//         int answeredAndMarkedForReview = (int) examResults.stream().filter(r -> Boolean.TRUE.equals(r.getAnswered()) && Boolean.TRUE.equals(r.getMarkedForReview())).count();
//         int notVisited = (int) examResults.stream().filter(r -> !Boolean.TRUE.equals(r.getVisited())).count();
 
//         // Count correct and wrong answers
//         int correctCount = (int) examResults.stream().filter(r -> "correct".equals(r.getValidateAnswer())).count();
//         int wrongCount = (int) examResults.stream().filter(r -> "wrong".equals(r.getValidateAnswer())).count();
 
//         ScheduledExam examEntity=scheduledExamRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId).orElse(null);
//         String difficultyLevel=examEntity!=null && examEntity.getDifficulty()!= null? examEntity.getDifficulty().name() : null;
//         String batchId=examEntity.getBatchId();
//         String branchId=examEntity.getBranchId();
//         Long tenantId=examEntity.getTenantId();
//         Long scheduledExamId=examEntity.getScheduledExamId();
//         Long educatorId=examEntity.getEducatorId();
//         // Build and save summary
//         ScheduledExamResultsSummary summary = ScheduledExamResultsSummary.builder()
//                 .userId(userId)
//                 .eduScheduledExamId(eduScheduledExamId)
//                 .batchId(Long.parseLong(batchId))
//                 .branchId(Long.parseLong(branchId))
//                 .scheduledExamId(scheduledExamId)
//                 .tenantId(tenantId)
//                 .educatorId(educatorId)
//                 .difficultyLevel(difficultyLevel)
//                 .totalQuestions(totalQuestions)
//                 .totalAnswered(totalAnswered)
//                 .notAnswered(notAnswered)
//                 .markedForReview(markedForReview)
//                 .answeredAndMarkedForReview(answeredAndMarkedForReview)
//                 .notVisited(notVisited)
//                 .correctedCount(correctCount)
//                 .wrongCount(wrongCount)
//                 .build();
//         scheduledExamResultsSummaryRepository.save(summary);
// 	    scheduledDifficultyWisePerformanceService.aggregateAndInsertDifficultyWisePerformance(userId, eduScheduledExamId);
//         System.out.println("ExamResultsSummary created for userId=" + userId + ", examId=" + eduScheduledExamId);
//     }
 
//     private String generateChapterSummary(int marks, int totalQuestions, int correct, int duration, BigDecimal percentage) {
//         StringBuilder summary = new StringBuilder();
       
//         // Add performance level
//         if (percentage.doubleValue() >= 90) {
//             summary.append("Excellent performance! ");
//         } else if (percentage.doubleValue() >= 75) {
//             summary.append("Very good performance! ");
//         } else if (percentage.doubleValue() >= 60) {
//             summary.append("Good performance. ");
//         } else if (percentage.doubleValue() >= 40) {
//             summary.append("Fair performance. ");
//         } else {
//             summary.append("Needs improvement. ");
//         }
 
//         // Add score details
//         summary.append(String.format("Scored %d out of %d possible marks. ", marks, totalQuestions * 4));
       
//         // Add accuracy details
//         int accuracy = totalQuestions > 0 ? (correct * 100) / totalQuestions : 0;
//         summary.append(String.format("Accuracy: %d%%. ", accuracy));
       
//         // Add time management feedback
//         int avgTimePerQuestion = totalQuestions > 0 ? duration / totalQuestions : 0;
//         if (avgTimePerQuestion <= 60) {
//             summary.append("Good time management. ");
//         } else if (avgTimePerQuestion <= 90) {
//             summary.append("Moderate time management. ");
//         } else {
//             summary.append("Time management needs improvement. ");
//         }
 
//         // Add recommendation
//         if (percentage.doubleValue() < 60) {
//             summary.append("Focus on understanding core concepts and practice more questions.");
//         } else if (percentage.doubleValue() < 80) {
//             summary.append("Keep practicing to improve accuracy and speed.");
//         } else {
//             summary.append("Maintain this performance level with regular practice.");
//         }
 
//         return summary.toString();
//     }
 
//     @Override
//     @Transactional
//     public void createChapterWiseResults(Long eduScheduledExamId, Long userId) {
//         // Optional<User> userOpt = userRepository.findById(userId);
//          Optional<ScheduledExam> examOpt = scheduledExamRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId);
//         // if (userOpt.isEmpty() || examOpt.isEmpty()) {
//         //     // System.out.println("User or Exam not found for chapter-wise results");
//         //     return;
//         // }
//         // User user = userOpt.get();
//         ScheduledExam exam = examOpt.get();
//         List<ScheduledExamResult> examResults = examResultRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId);
//         if (examResults == null || examResults.isEmpty()) {
//             // System.out.println("No exam results found for chapter-wise results");
//             return;
//         }
//         // Group by chapter
//         Map<String, List<ScheduledExamResult>> resultsByChapter = examResults.stream()
//             .collect(java.util.stream.Collectors.groupingBy(ScheduledExamResult::getChapter));
//         for (Map.Entry<String, List<ScheduledExamResult>> entry : resultsByChapter.entrySet()) {
//             String chapterName = entry.getKey();
//             List<ScheduledExamResult> chapterResults = entry.getValue();
//             // List<ChapterResult> chapterWiseResults = entry.getValue();
 
//             // Get the first ChapterResult for this group
//             ScheduledExamResult firstResult = chapterResults.get(0);
 
//             // If you have a corresponding ChapterResult entity, get the values from it
//             String chapterNameFromResult = firstResult.getChapter();
//             String subject = firstResult.getSubject();
 
//             Chapter chapterEntity = chapterRepository.findBySubjectAndChapter(subject, chapterNameFromResult).orElse(null);
//             String chapterId = chapterEntity != null ? chapterEntity.getChapterId() : null;
 
//             int marks = chapterResults.stream()
//                 .mapToInt(er -> {
//                     if ("correct".equals(er.getValidateAnswer())) return 4;
//                     if ("wrong".equals(er.getValidateAnswer())) return -1;
//                     return 0;
//                 })
//                 .sum();
//             int duration = chapterResults.stream()
//                 .map(er -> er.getDuration() != null ? (int) er.getDuration().getSeconds() : 0)
//                 .reduce(0, Integer::sum);
         
//                 int totalQuestions = chapterResults.size();
//             int totalMarks = chapterResults.size()*4;
//             int correct = (int) chapterResults.stream().filter(er -> "correct".equals(er.getValidateAnswer())).count();
//             int wrong = (int) chapterResults.stream().filter(er -> "wrong".equals(er.getValidateAnswer())).count();
//             int percentage = totalQuestions > 0 ? (marks * 100) / totalMarks : 0;
//             // for (ExamResult result : chapterResults) {
//             //     int percentage = result.getPercentage();
//             //     // use percentage here
//             // }
           
 
 
           
//             // Generate summary text
//             String summary = generateChapterSummary(marks, totalQuestions, correct, duration,BigDecimal.valueOf(percentage));
           
//             ScheduledChapterWiseResult cwr = ScheduledChapterWiseResult.builder()
//                 .id(UUID.randomUUID())
//                 .userId(userId)
//                 .eduScheduledExamId(eduScheduledExamId)
//                 .scheduledExamId(exam.getScheduledExamId())
//                 .tenantId(exam.getTenantId())
//                 .batchId(Long.parseLong(exam.getBatchId()))
//                 .branchId(Long.parseLong(exam.getBranchId()))
//                 .educatorId(exam.getEducatorId())
//                 .chapterId(chapterId)
//                 .chapter(chapterNameFromResult)
//                 .subject(subject)
//                 .marks(marks)
//                 .duration(duration)
//                 .percentage(BigDecimal.valueOf(Math.max(percentage, 0)))
 
//                 .summary(summary)
//                 .difficultyLevel(exam.getDifficulty() != null ? exam.getDifficulty().name() : null)
//                 .build();
//                 scheduledChapterWiseResultRepository.save(cwr);
//         }
//         System.out.println("ChapterWiseResults created for userId=" + userId + ", eduScheduledExamId=" + eduScheduledExamId);
//     }
// }
package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.ScheduledDifficultyWisePerformanceService;
import com.brihathi.Multi_Tenant.service.ScheduledExamResultSummaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.math.BigDecimal;
import java.lang.StringBuilder;
import java.util.stream.Collectors;

@Service
public class ScheduledExamResultSummaryServiceImpl
        implements ScheduledExamResultSummaryService {

    @Autowired
    private ScheduledExamResultsSummaryRepository summaryRepository;

    @Autowired
    private ScheduledExamRepository scheduledExamRepository;

    @Autowired
    private ScheduledExamResultRepository scheduledExamResultRepository;

    @Autowired
    private ScheduledChapterWiseResultRepository chapterWiseResultRepository;

    @Autowired
    private ChapterRepository chapterRepository;
   


    /* =====================================================
       EXAM RESULT SUMMARY (PORTED FROM OLD APP)
       ===================================================== */

//     @Override
//     @Transactional
//     public void createExamResultSummary(Long eduScheduledExamId, Long userId) {

//         // ScheduledExam scheduledExam = scheduledExamRepository
//         // .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
//         // .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));




//  // 2️⃣ Fetch scheduled exam
//  ScheduledExam scheduledExam =
//  scheduledExamRepository.findByEduScheduledExamId(eduScheduledExamId)
//  // .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
//          .orElse(null);

//          Long scheduledExamId = scheduledExam.getScheduledExamId();
//         // 1️⃣ Idempotency check
//         if (summaryRepository
//                 .findByScheduledExamIdAndUserId(scheduledExamId, userId)
//                 .isPresent()) {
//             return;
//         }

//         // // 2️⃣ Fetch scheduled exam
//         // ScheduledExam scheduledExam =
//         //         scheduledExamRepository.findByEduScheduledExamId(eduScheduledExamId)
//         //         // .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
//         //                 .orElse(null);

//         if (scheduledExam == null) {
//             return;
//         }

//         // 3️⃣ Fetch raw exam question results (DB only)
//         List<ScheduledExamResult> examResults =
//                 scheduledExamResultRepository
//                         .findByEduScheduledExamIdAndUserId(
//                                 eduScheduledExamId, userId);

//         if (examResults == null || examResults.isEmpty()) {
//             return;
//         }

//         // 4️⃣ Aggregations (EXACT OLD LOGIC)
//         int totalQuestions = examResults.size();

//         int totalAnswered = (int) examResults.stream()
//                 .filter(ScheduledExamResult::getAnswered)
//                 .count();

//         int notAnswered = totalQuestions - totalAnswered;

//         int markedForReview = (int) examResults.stream()
//                 .filter(ScheduledExamResult::getMarkedForReview)
//                 .count();

//         int answeredAndMarkedForReview = (int) examResults.stream()
//                 .filter(r ->
//                         Boolean.TRUE.equals(r.getAnswered())
//                                 && Boolean.TRUE.equals(r.getMarkedForReview()))
//                 .count();

//         int notVisited = (int) examResults.stream()
//                 .filter(r -> !Boolean.TRUE.equals(r.getVisited()))
//                 .count();

//         int correctCount = (int) examResults.stream()
//                 .filter(r -> "correct".equals(r.getValidateAnswer()))
//                 .count();

//         int wrongCount = (int) examResults.stream()
//                 .filter(r -> "wrong".equals(r.getValidateAnswer()))
//                 .count();

//         String difficultyLevel =
//                 scheduledExam.getDifficulty() != null
//                         ? scheduledExam.getDifficulty().name()
//                         : null;

//         // 5️⃣ Build analytics entity
//         ScheduledExamResultsSummary summary =
//                 ScheduledExamResultsSummary.builder()
//                         .scheduledExamId(scheduledExam.getScheduledExamId())
//                         .eduScheduledExamId(eduScheduledExamId)
//                         .userId(userId)
//                         .tenantId(scheduledExam.getTenantId())
//                         .educatorId(scheduledExam.getEducatorId())
//                         .branchId(scheduledExam.getBranchId())
//                         .batchId(scheduledExam.getBatchId())
//                         .difficultyLevel(difficultyLevel)
//                         .totalQuestions(totalQuestions)
//                         .totalAnswered(totalAnswered)
//                         .notAnswered(notAnswered)
//                         .markedForReview(markedForReview)
//                         .answeredAndMarkedForReview(answeredAndMarkedForReview)
//                         .notVisited(notVisited)
//                         .correctedCount(correctCount)
//                         .wrongCount(wrongCount)
//                         .build();

//         // 6️⃣ Insert (safe)
//         summaryRepository.save(summary);
//     }

//     /* =====================================================
//        CHAPTER-WISE WILL BE DONE NEXT (NOT HERE)
//        ===================================================== */

//        @Override
//        @Transactional
//        public void createChapterWiseResults(Long eduScheduledExamId, Long userId) {
   
//            // 1️⃣ Fetch scheduled exam
//            ScheduledExam scheduledExam =
//                    scheduledExamRepository
//                            .findByEduScheduledExamId(eduScheduledExamId)
//                            .orElse(null);
   
//            if (scheduledExam == null) return;
   
//            // 2️⃣ Fetch raw question-level results
//            List<ScheduledExamResult> examResults =
//                    scheduledExamResultRepository
//                            .findByEduScheduledExamIdAndUserId(
//                                    eduScheduledExamId, userId);
   
//            if (examResults == null || examResults.isEmpty()) return;
   
//            // 3️⃣ Group by chapter
//            Map<String, List<ScheduledExamResult>> resultsByChapter =
//                    examResults.stream()
//                            .collect(Collectors.groupingBy(
//                                    ScheduledExamResult::getChapter));
   
//            // 4️⃣ Loop chapter-wise
//            for (Map.Entry<String, List<ScheduledExamResult>> entry
//                    : resultsByChapter.entrySet()) {
   
//                String chapterName = entry.getKey();
//                List<ScheduledExamResult> chapterResults = entry.getValue();
   
//                ScheduledExamResult first = chapterResults.get(0);
//                String subject = first.getSubject();

//                Long scheduledExamId = scheduledExam.getScheduledExamId();
   
//                // 5️⃣ Idempotency check
//                boolean exists =
//                        chapterWiseResultRepository
//                                .findByScheduledExamIdAndUserIdAndSubjectAndChapter(
//                                        scheduledExamId,
//                                        userId,
//                                        subject,
//                                        chapterName
//                                ).isPresent();
   
//                if (exists) continue;
   
//                // 6️⃣ Chapter master lookup
//                String chapterId = chapterRepository
//                        .findBySubjectAndChapter(subject, chapterName)
//                        .map(Chapter::getChapterId)
//                        .orElse(null);
   
//                // 7️⃣ Calculations (OLD LOGIC)
//                int marks = chapterResults.stream()
//                        .mapToInt(r -> {
//                            if ("correct".equals(r.getValidateAnswer())) return 4;
//                            if ("wrong".equals(r.getValidateAnswer())) return -1;
//                            return 0;
//                        })
//                        .sum();
   
//                int duration = chapterResults.stream()
//                        .mapToInt(r ->
//                                r.getDuration() != null
//                                        ? (int) r.getDuration().getSeconds()
//                                        : 0)
//                        .sum();
   
//                int totalQuestions = chapterResults.size();
//                int totalMarks = totalQuestions * 4;
   
//                int correct = (int) chapterResults.stream()
//                        .filter(r -> "correct".equals(r.getValidateAnswer()))
//                        .count();
   
//                int percentage =
//                        totalMarks > 0
//                                ? (marks * 100) / totalMarks
//                                : 0;
   
//                // 8️⃣ Summary text (same logic)
//                String summary =
//                        generateChapterSummary(
//                                marks,
//                                totalQuestions,
//                                correct,
//                                duration,
//                                BigDecimal.valueOf(percentage)
//                        );
   
//                // 9️⃣ Build entity
//                ScheduledChapterWiseResult result =
//                        ScheduledChapterWiseResult.builder()
//                                .id(UUID.randomUUID())
//                                .scheduledExamId(scheduledExam.getScheduledExamId())
//                                .eduScheduledExamId(eduScheduledExamId)
//                                .userId(userId)
//                                .tenantId(scheduledExam.getTenantId())
//                                .educatorId(scheduledExam.getEducatorId())
//                                .branchId(scheduledExam.getBranchId())
//                                .batchId(scheduledExam.getBatchId())
//                                .subject(subject)
//                                .chapter(chapterName)
//                                .chapterId(chapterId)
//                                .marks(marks)
//                                .duration(duration)
//                                .percentage(BigDecimal.valueOf(Math.max(percentage, 0)))
//                                .difficultyLevel(
//                                        scheduledExam.getDifficulty() != null
//                                                ? scheduledExam.getDifficulty().name()
//                                                : null
//                                )
//                                .summary(summary)
//                                .build();
   
//                // 🔟 Save
//                chapterWiseResultRepository.save(result);
//            }
//        }
   
//        /* =====================================================
//           SAME SUMMARY METHOD (UNCHANGED)
//           ===================================================== */
   
//        private String generateChapterSummary(
//                int marks,
//                int totalQuestions,
//                int correct,
//                int duration,
//                BigDecimal percentage) {
   
//            StringBuilder summary = new StringBuilder();
   
//            if (percentage.doubleValue() >= 90) {
//                summary.append("Excellent performance! ");
//            } else if (percentage.doubleValue() >= 75) {
//                summary.append("Very good performance! ");
//            } else if (percentage.doubleValue() >= 60) {
//                summary.append("Good performance. ");
//            } else if (percentage.doubleValue() >= 40) {
//                summary.append("Fair performance. ");
//            } else {
//                summary.append("Needs improvement. ");
//            }
   
//            summary.append(
//                    String.format(
//                            "Scored %d out of %d possible marks. ",
//                            marks,
//                            totalQuestions * 4
//                    )
//            );
   
//            int accuracy =
//                    totalQuestions > 0
//                            ? (correct * 100) / totalQuestions
//                            : 0;
   
//            summary.append(
//                    String.format("Accuracy: %d%%. ", accuracy)
//            );
   
//            int avgTime =
//                    totalQuestions > 0
//                            ? duration / totalQuestions
//                            : 0;
   
//            if (avgTime <= 60) {
//                summary.append("Good time management. ");
//            } else if (avgTime <= 90) {
//                summary.append("Moderate time management. ");
//            } else {
//                summary.append("Time management needs improvement. ");
//            }
   
//            if (percentage.doubleValue() < 60) {
//                summary.append(
//                        "Focus on understanding core concepts and practice more questions."
//                );
//            } else if (percentage.doubleValue() < 80) {
//                summary.append(
//                        "Keep practicing to improve accuracy and speed."
//                );
//            } else {
//                summary.append(
//                        "Maintain this performance level with regular practice."
//                );
//            }
   
//            return summary.toString();
//        }

@Override
@Transactional
public void createExamResultSummary(Long eduScheduledExamId, Long userId) {

    // ✅ ALWAYS FETCH STUDENT EXAM
    ScheduledExam scheduledExam = scheduledExamRepository
            .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
            .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

    Long scheduledExamId = scheduledExam.getScheduledExamId();

    // ✅ Idempotency
    if (summaryRepository
            .findByScheduledExamIdAndUserId(scheduledExamId, userId)
            .isPresent()) return;

    // ❌ OLD (WRONG)
    // findByEduScheduledExamIdAndUserId

    // ✅ NEW (CORRECT)
    List<ScheduledExamResult> examResults =
            scheduledExamResultRepository
                    .findByScheduledExamIdAndUserId(scheduledExamId, userId);

    if (examResults.isEmpty()) return;

    int totalQuestions = examResults.size();
    int totalAnswered = (int) examResults.stream().filter(ScheduledExamResult::getAnswered).count();
    int notAnswered = totalQuestions - totalAnswered;
    int markedForReview = (int) examResults.stream().filter(ScheduledExamResult::getMarkedForReview).count();
    int answeredAndMarkedForReview = (int) examResults.stream()
            .filter(r -> Boolean.TRUE.equals(r.getAnswered()) && Boolean.TRUE.equals(r.getMarkedForReview()))
            .count();
    int notVisited = (int) examResults.stream().filter(r -> !Boolean.TRUE.equals(r.getVisited())).count();
    int correctCount = (int) examResults.stream().filter(r -> "correct".equals(r.getValidateAnswer())).count();
    int wrongCount = (int) examResults.stream().filter(r -> "wrong".equals(r.getValidateAnswer())).count();

    ScheduledExamResultsSummary summary = ScheduledExamResultsSummary.builder()
            .scheduledExamId(scheduledExamId)
            .eduScheduledExamId(eduScheduledExamId)
            .userId(userId)
            .tenantId(scheduledExam.getTenantId())
            .educatorId(scheduledExam.getEducatorId())
            .branchId(scheduledExam.getBranchId())
            .batchId(scheduledExam.getBatchId())
            .difficultyLevel(scheduledExam.getDifficulty().name())
            .totalQuestions(totalQuestions)
            .totalAnswered(totalAnswered)
            .notAnswered(notAnswered)
            .markedForReview(markedForReview)
            .answeredAndMarkedForReview(answeredAndMarkedForReview)
            .notVisited(notVisited)
            .correctedCount(correctCount)
            .wrongCount(wrongCount)
            .build();

    summaryRepository.save(summary);


    
    // try {
    //     scheduledDifficultyWisePerformanceService
    //         .aggregateAndInsertDifficultyWisePerformance(userId, scheduledExamId);
    // } catch (Exception e) {
    //     System.out.println("❌ Difficulty insert failed but summary safe: " + e.getMessage());
    // }
}


@Override
@Transactional
public void createChapterWiseResults(Long eduScheduledExamId, Long userId) {

    ScheduledExam scheduledExam = scheduledExamRepository
            .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
            .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

    Long scheduledExamId = scheduledExam.getScheduledExamId();

    // ❌ OLD
    // findByEduScheduledExamIdAndUserId

    // ✅ NEW
    List<ScheduledExamResult> examResults =
            scheduledExamResultRepository
                    .findByScheduledExamIdAndUserId(scheduledExamId, userId);

    if (examResults.isEmpty()) return;

    Map<String, List<ScheduledExamResult>> resultsByChapter =
            examResults.stream().collect(Collectors.groupingBy(ScheduledExamResult::getChapter));

    for (var entry : resultsByChapter.entrySet()) {

        String chapterName = entry.getKey();
        List<ScheduledExamResult> chapterResults = entry.getValue();
        String subject = chapterResults.get(0).getSubject();

        boolean exists = chapterWiseResultRepository
                .findByScheduledExamIdAndUserIdAndSubjectAndChapter(
                        scheduledExamId, userId, subject, chapterName)
                .isPresent();

        if (exists) continue;

        String chapterId = chapterRepository
                .findBySubjectAndChapter(subject, chapterName)
                .map(Chapter::getChapterId)
                .orElse(null);

        int marks = chapterResults.stream().mapToInt(r -> {
            if ("correct".equals(r.getValidateAnswer())) return 4;
            if ("wrong".equals(r.getValidateAnswer())) return -1;
            return 0;
        }).sum();

        int duration = chapterResults.stream()
                .mapToInt(r -> r.getDuration() != null ? (int) r.getDuration().getSeconds() : 0)
                .sum();

        int totalQuestions = chapterResults.size();
        int totalMarks = totalQuestions * 4;
        int correct = (int) chapterResults.stream()
                .filter(r -> "correct".equals(r.getValidateAnswer())).count();

        int percentage = totalMarks > 0 ? (marks * 100) / totalMarks : 0;

        ScheduledChapterWiseResult result = ScheduledChapterWiseResult.builder()
                .id(UUID.randomUUID())
                .scheduledExamId(scheduledExamId)
                .eduScheduledExamId(eduScheduledExamId)
                .userId(userId)
                .tenantId(scheduledExam.getTenantId())
                .educatorId(scheduledExam.getEducatorId())
                .branchId(scheduledExam.getBranchId())
                .batchId(scheduledExam.getBatchId())
                .subject(subject)
                .chapter(chapterName)
                .chapterId(chapterId)
                .marks(marks)
                .duration(duration)
                .percentage(BigDecimal.valueOf(Math.max(percentage, 0)))
                .difficultyLevel(scheduledExam.getDifficulty().name())
                .summary(generateChapterSummary(marks, totalQuestions, correct, duration, BigDecimal.valueOf(percentage)))
                .build();

        chapterWiseResultRepository.save(result);
    }
}

private String generateChapterSummary(
    int marks,
    int totalQuestions,
    int correct,
    int duration,
    BigDecimal percentage) {

StringBuilder summary = new StringBuilder();

if (percentage.doubleValue() >= 90) {
    summary.append("Excellent performance! ");
} else if (percentage.doubleValue() >= 75) {
    summary.append("Very good performance! ");
} else if (percentage.doubleValue() >= 60) {
    summary.append("Good performance. ");
} else if (percentage.doubleValue() >= 40) {
    summary.append("Fair performance. ");
} else {
    summary.append("Needs improvement. ");
}

summary.append(String.format(
        "Scored %d out of %d possible marks. ",
        marks,
        totalQuestions * 4
));

int accuracy = totalQuestions > 0 ? (correct * 100) / totalQuestions : 0;
summary.append(String.format("Accuracy: %d%%. ", accuracy));

int avgTime = totalQuestions > 0 ? duration / totalQuestions : 0;

if (avgTime <= 60) {
    summary.append("Good time management. ");
} else if (avgTime <= 90) {
    summary.append("Moderate time management. ");
} else {
    summary.append("Time management needs improvement. ");
}

if (percentage.doubleValue() < 60) {
    summary.append("Focus on understanding core concepts and practice more questions.");
} else if (percentage.doubleValue() < 80) {
    summary.append("Keep practicing to improve accuracy and speed.");
} else {
    summary.append("Maintain this performance level with regular practice.");
}

return summary.toString();
}


}
