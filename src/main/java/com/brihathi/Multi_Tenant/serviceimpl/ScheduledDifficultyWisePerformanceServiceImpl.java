package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.ScheuledDifficultyWisePerformance;
import com.brihathi.Multi_Tenant.entity.ScheduledExamResultsSummary;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.ScheduledExam;
import com.brihathi.Multi_Tenant.repository.ScheduledDifficultyWisePerformanceRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamResultsSummaryRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.service.ScheduledDifficultyWisePerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;


 
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.math.RoundingMode; 
// @Service
// public class ScheduledDifficultyWisePerformanceServiceImpl implements ScheduledDifficultyWisePerformanceService {
//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private ScheduledExamResultsSummaryRepository scheduledExamResultsSummaryRepository;
//     @Autowired
//     private ScheduledExamRepository scheduledExamRepository;
 
//     @Autowired
//     private ScheduledDifficultyWisePerformanceRepository scheduledDifficultyWisePerformanceRepository;
    
//     @Override
//     @Transactional
//     public void aggregateAndInsertDifficultyWisePerformance(Long userId, Long eduScheduledExamId) {
//         ScheduledExam scheduledExam = scheduledExamRepository
//         .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
//         .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

// Long scheduledExamId = scheduledExam.getScheduledExamId();
    
//         // 1️⃣ Fetch exam summary (ONLY ONE ROW expected)
//         Optional<ScheduledExamResultsSummary> summaryOpt =
//                 scheduledExamResultsSummaryRepository
//                         // .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId);
//                         .findByScheduledExamIdAndUserId(scheduledExamId, userId);
    
//         if (summaryOpt.isEmpty()) return;
    
//         ScheduledExamResultsSummary summary = summaryOpt.get();
    
//         // 2️⃣ Difficulty level must exist
//         String difficultyLevel = summary.getDifficultyLevel();
//         if (difficultyLevel == null) return;
    
//         // 3️⃣ Calculate percentages
//         double avgCorrect = roundToFiveTwo(
//                 percent(summary.getCorrectedCount(), summary.getTotalQuestions())
//         );
    
//         double avgWrong = roundToFiveTwo(
//                 percent(summary.getWrongCount(), summary.getTotalQuestions())
//         );
    
//         double avgUnattempted = roundToFiveTwo(
//                 percent(summary.getNotAnswered(), summary.getTotalQuestions())
//         );
    
//         // 4️⃣ Save performance rows (3 types)
//         savePerformance(userId, eduScheduledExamId, difficultyLevel, "CorrectAnswers", avgCorrect);
//         savePerformance(userId, eduScheduledExamId, difficultyLevel, "WrongAnswers", avgWrong);
//         savePerformance(userId, eduScheduledExamId, difficultyLevel, "UnAttempted", avgUnattempted);
//     }
    
 
//     private static double roundToFiveTwo(double value) {
//         return new BigDecimal(value)
//                 .setScale(2, RoundingMode.HALF_UP)  // 2 decimal places
//                 .doubleValue();
//     }
 
//     private double percent(Integer count, Integer total) {
//         if (count == null || total == null || total == 0) return 0.0;
//         return (count * 100.0) / total;
//     }
   
   
//     private void savePerformance(Long userId, Long eduScheduledExamId, String difficultyLevel, String type, double percent) {
//         //BigDecimal percentage = BigDecimal.valueOf(percent).setScale(2, BigDecimal.ROUND_HALF_UP);
//         ScheduledExam exam = scheduledExamRepository.findById(eduScheduledExamId)
//         .orElseThrow(() -> new RuntimeException("Exam not found for ID: " + eduScheduledExamId));
   
//         BigDecimal percentage = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
//         percentage = percentage.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : percentage;
//         ScheuledDifficultyWisePerformance perf = ScheuledDifficultyWisePerformance.builder()
//             .uuid(UUID.randomUUID())
//             .userId(userId)
//             .eduScheduledExamId(eduScheduledExamId)
//             .educatorId(exam.getEducatorId())
//             .tenantId(exam.getTenantId())
//             .branchId(exam.getBranchId())
//             .batchId(exam.getBatchId())
//             .difficultyLevel(difficultyLevel)
//             .type(type)
//             .percentage(percentage)
//             .build();
//         scheduledDifficultyWisePerformanceRepository.save(perf);
//         System.out.println("response saved");
       
//     }
// }
 
@Service
public class ScheduledDifficultyWisePerformanceServiceImpl
        implements ScheduledDifficultyWisePerformanceService {

    @Autowired private ScheduledExamResultsSummaryRepository scheduledExamResultsSummaryRepository;
    @Autowired private ScheduledExamRepository  scheduledExamRepository;
    @Autowired private ScheduledDifficultyWisePerformanceRepository scheduledDifficultyWisePerformanceRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggregateAndInsertDifficultyWisePerformance(Long userId, Long scheduledExamId) {
    
        ScheduledExam exam = scheduledExamRepository
                .findByScheduledExamIdAndUserId(scheduledExamId, userId)
                .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));
    
        // Long scheduledExamId = exam.getScheduledExamId();
    
        // ✅ GET ALL SUMMARIES (NOT SINGLE)
        List<ScheduledExamResultsSummary> summaries =
                scheduledExamResultsSummaryRepository
                        .findAllByScheduledExamIdAndUserId(scheduledExamId, userId);
    
        if (summaries.isEmpty()) return;
    
        // ✅ GROUP BY DIFFICULTY
        Map<String, List<ScheduledExamResultsSummary>> byDifficulty =
                summaries.stream()
                        .filter(s -> s.getDifficultyLevel() != null)
                        .collect(Collectors.groupingBy(ScheduledExamResultsSummary::getDifficultyLevel));
    
        for (Map.Entry<String, List<ScheduledExamResultsSummary>> entry : byDifficulty.entrySet()) {
    
            String difficultyLevel = entry.getKey();
            List<ScheduledExamResultsSummary> list = entry.getValue();
    
            double avgCorrect = roundToFiveTwo(
                    list.stream().mapToDouble(s -> percent(s.getCorrectedCount(), s.getTotalQuestions()))
                            .average().orElse(0)
            );
    
            double avgWrong = roundToFiveTwo(
                    list.stream().mapToDouble(s -> percent(s.getWrongCount(), s.getTotalQuestions()))
                            .average().orElse(0)
            );
    
            double avgUnattempted = roundToFiveTwo(
                    list.stream().mapToDouble(s -> percent(s.getNotAnswered(), s.getTotalQuestions()))
                            .average().orElse(0)
            );
    
            savePerformance(userId, exam, difficultyLevel, "CorrectAnswers", avgCorrect);
            savePerformance(userId, exam, difficultyLevel, "WrongAnswers", avgWrong);
            savePerformance(userId, exam, difficultyLevel, "UnAttempted", avgUnattempted);
        }
    }
    
    
    private static double roundToFiveTwo(double value) {
        return new BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP)  // 2 decimal places
                .doubleValue();
    }
 
    private double percent(Integer count, Integer total) {
        if (count == null || total == null || total == 0) return 0.0;
        return (count * 100.0) / total;
    }

//     private void savePerformance(Long userId, ScheduledExam exam,
//         String difficultyLevel, String type, double percent) {

// BigDecimal percentage = BigDecimal.valueOf(percent)
// .setScale(2, RoundingMode.HALF_UP);

// ScheuledDifficultyWisePerformance perf = ScheuledDifficultyWisePerformance.builder()
// .uuid(UUID.randomUUID())
// .userId(userId)
// .scheduledExamId(exam.getScheduledExamId())
// .eduScheduledExamId(exam.getEduScheduledExamId())
// .tenantId(exam.getTenantId())
// .educatorId(exam.getEducatorId())
// .branchId(exam.getBranchId())
// .batchId(exam.getBatchId())
// .difficultyLevel(difficultyLevel)
// .type(type)
// .percentage(percentage.max(BigDecimal.ZERO))
// .build();

// scheduledDifficultyWisePerformanceRepository.save(perf);
// }


private void savePerformance(Long userId, ScheduledExam exam,
        String difficultyLevel, String type, double percent) {

Optional<ScheuledDifficultyWisePerformance> existing =
scheduledDifficultyWisePerformanceRepository
.findByUserIdAndScheduledExamIdAndDifficultyLevelAndType(
       userId,
       exam.getScheduledExamId(),
       difficultyLevel,
       type
);

BigDecimal percentage = BigDecimal.valueOf(percent)
.setScale(2, RoundingMode.HALF_UP)
.max(BigDecimal.ZERO);

ScheuledDifficultyWisePerformance perf = existing.orElse(
ScheuledDifficultyWisePerformance.builder()
.uuid(UUID.randomUUID())
.userId(userId)
.scheduledExamId(exam.getScheduledExamId())
.eduScheduledExamId(exam.getEduScheduledExamId())
.tenantId(exam.getTenantId())
.educatorId(exam.getEducatorId())
.branchId(exam.getBranchId())
.batchId(exam.getBatchId())
.difficultyLevel(difficultyLevel)
.type(type)
.build()
);

perf.setPercentage(percentage);

scheduledDifficultyWisePerformanceRepository.save(perf);
}

}
