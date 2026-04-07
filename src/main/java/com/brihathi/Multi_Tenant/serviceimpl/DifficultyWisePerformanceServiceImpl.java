
package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.DifficultyWisePerformance;
import com.brihathi.Multi_Tenant.entity.ExamResultsSummary;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.repository.DifficultyWisePerformanceRepository;
import com.brihathi.Multi_Tenant.repository.ExamResultsSummaryRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.service.DifficultyWisePerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.brihathi.Multi_Tenant.repository.ExamRepository;
 
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.math.RoundingMode; 
@Service
public class DifficultyWisePerformanceServiceImpl implements DifficultyWisePerformanceService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamResultsSummaryRepository summaryRepository;
    @Autowired
    private ExamRepository examRepository;
 
    @Autowired
    private DifficultyWisePerformanceRepository difficultyWisePerformanceRepository;
    
     @Override
    public void aggregateAndInsertDifficultyWisePerformance(Long userId,Long examId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found for ID: " + userId));
 
        Exam exam = examRepository.findById(examId)
        .orElseThrow(() -> new RuntimeException("Exam not found for ID: " + examId));
   
        // Only get summaries for this user and exam
        List<ExamResultsSummary> summaries = summaryRepository.findAllByUser_UserIdAndExam_ExamId(userId, examId);
        if (summaries.isEmpty()) return;
        Map<String, List<ExamResultsSummary>> byDifficulty = summaries.stream()
            .filter(s -> s.getDifficultyLevel() != null)
            .collect(Collectors.groupingBy(ExamResultsSummary::getDifficultyLevel));
        for (Map.Entry<String, List<ExamResultsSummary>> entry : byDifficulty.entrySet()) {
            String difficultyLevel = entry.getKey();
            List<ExamResultsSummary> list = entry.getValue();
double avgCorrect = roundToFiveTwo(
    list.stream()
        .mapToDouble(s -> percent(s.getCorrectedCount(), s.getTotalQuestions()))
        .average()
        .orElse(0.0) // extract double from OptionalDouble
);
System.out.println("average correct percentage"+avgCorrect);
 
double avgWrong = roundToFiveTwo(
    list.stream()
        .mapToDouble(s -> percent(s.getWrongCount(), s.getTotalQuestions()))
        .average()
        .orElse(0.0)
);
System.out.println("average wrong percentage"+avgWrong);
 
double avgUnattempted = roundToFiveTwo(
    list.stream()
        .mapToDouble(s -> percent(s.getNotAnswered(), s.getTotalQuestions()))
        .average()
        .orElse(0.0)
);
System.out.println("average unattempted percentage"+avgUnattempted);
 
         // Save for each type
            savePerformance(user,exam,difficultyLevel, "CorrectAnswers", avgCorrect);
            System.out.println("save response method calleds");
            savePerformance(user,exam, difficultyLevel, "WrongAnswers", avgWrong);
            savePerformance(user,exam, difficultyLevel, "UnAttempted", avgUnattempted);
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
   
   
    private void savePerformance(User user,Exam exam, String difficultyLevel, String type, double percent) {
        //BigDecimal percentage = BigDecimal.valueOf(percent).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal percentage = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
        percentage = percentage.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : percentage;
        DifficultyWisePerformance perf = DifficultyWisePerformance.builder()
            .uuid(UUID.randomUUID())
            .user(user)
            .tenantId(exam.getTenantId())
            .exam(exam)
            .difficultyLevel(difficultyLevel)
            .type(type)
            .percentage(percentage)
            .build();
        difficultyWisePerformanceRepository.save(perf);
        System.out.println("response saved");
       
    }
}
 
