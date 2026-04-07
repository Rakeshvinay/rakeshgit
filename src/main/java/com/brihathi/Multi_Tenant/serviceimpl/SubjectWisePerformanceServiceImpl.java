package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.dto.ChapterWisePerformanceDTO;
import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceDTO;
import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.SubjectWisePerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
 
@Service
public class SubjectWisePerformanceServiceImpl implements SubjectWisePerformanceService {
 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private ExamResultRepository examResultRepository;
 
    @Autowired
    private SubjectWisePerformanceBarRepository subjectWisePerformanceBarRepository;
 
    @Autowired
    private ExamRepository examRepository;
 
    @Autowired
    private ChapterWiseResultRepository chapterWiseResultRepository;
 
    @Override
    public void aggregateAndInsertSubjectWisePerformance(Long examId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Exam exam = examRepository.findByExamIdAndUser_UserId(examId, userId)
                .orElseThrow(() -> new RuntimeException("Exam not found for userId=" + userId + ", examId=" + examId));

        // Fetch only this user's results for this exam
        List<ExamResult> results = examResultRepository.findByExamIdAndUserId(examId, userId);
 
        Map<String, List<ExamResult>> bySubject = results.stream()
            .collect(Collectors.groupingBy(ExamResult::getSubject));
 
        for (Map.Entry<String, List<ExamResult>> entry : bySubject.entrySet()) {
            String subject = entry.getKey();
            List<ExamResult> subjectResults = entry.getValue();
 
            int totalMarks = subjectResults.stream()
                .mapToInt(er -> {
                    String validate = er.getValidateAnswer();
                    if (validate != null && "correct".equalsIgnoreCase(validate)) return 4;
                    if (validate != null && "wrong".equalsIgnoreCase(validate)) return -1;
                    return 0;
                }).sum();
 
            int totalPossibleMarks = subjectResults.size() * 4;
            BigDecimal percentage = totalPossibleMarks > 0 ?
                    BigDecimal.valueOf((double) totalMarks / totalPossibleMarks * 100) :
                    BigDecimal.ZERO;
 
            percentage = percentage.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : percentage;
 
            SubjectWisePerformanceBar performanceBar = SubjectWisePerformanceBar.builder()
                .uuid(UUID.randomUUID())
                .tenantId(exam.getTenantId())
                .user(user)
                .exam(exam)
                .difficultyLevel(exam.getDifficulty() != null ? exam.getDifficulty().name() : null)
                .percentage(percentage)
                .subject(subject)
                .build();
 
            subjectWisePerformanceBarRepository.save(performanceBar);
        }
 
        System.out.println("SubjectWisePerformanceBar created for userId=" + userId + ", examId=" + examId);
    }
 
    @Override
    public Map<String, Object> getIndividualSubjectwiseAnalytics(Long userId,  String subject, LocalDateTime startDate, LocalDateTime endDate) {
        List<SubjectWisePerformanceDTO> subjectStats = subjectWisePerformanceBarRepository
            .getSubjectWisePerformanceByUser(userId,startDate, endDate);
 
        List<Map<String, Object>> bars = subjectStats.stream()
            .map(dto -> {
                Map<String, Object> map = new HashMap<>();
                map.put("subject", dto.getSubject());
                map.put("percentage", dto.getAveragePercentage() != null ? Math.round(dto.getAveragePercentage()) : 0);
                return map;
            })
            .collect(Collectors.toList());
 
        List<Map<String, Object>> aiInsights = subjectStats.stream()
            .map(dto -> {
                Map<String, Object> insight = new HashMap<>();
                String subj = dto.getSubject();
                int percent = dto.getAveragePercentage() != null ? (int) Math.round(dto.getAveragePercentage()) : 0;
                String performance;
                if (percent >= 80) performance = "Well";
                else if (percent >= 70) performance = "Good";
                else if (percent >= 60) performance = "Average";
                else performance = "Needs Improvement";
                insight.put("subject", subj);
                insight.put("performance", performance);
 
                List<String> weakChapters = new ArrayList<>();
                try {
                    List<ChapterWiseResult> chapterResults = chapterWiseResultRepository
                        .findByUser_UserIdAndSubject(userId, subj);
                    chapterResults = chapterResults.stream()
                        .filter(cr -> cr.getCreatedAt() != null
                                && !cr.getCreatedAt().isBefore(startDate)
                                && !cr.getCreatedAt().isAfter(endDate))
                        .filter(cr -> cr.getChapter() != null)
                        .collect(Collectors.toList());
 
                    Map<String, Double> chapterAverages = chapterResults.stream()
                        .collect(Collectors.groupingBy(
                            ChapterWiseResult::getChapter,
                            Collectors.averagingDouble(cr -> cr.getPercentage() != null ? cr.getPercentage().doubleValue() : 0.0)
                        ));
 
                    weakChapters = chapterAverages.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(3)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
 
                } catch (Exception ex) {
                    // Log or fallback
                }
 
                insight.put("needToImprove", weakChapters);
                return insight;
            })
            .collect(Collectors.toList());
 
        List<String> details = new ArrayList<>();
        if (subject != null) {
            try {
                List<ChapterWiseResult> chapterResults = chapterWiseResultRepository
                    .findByUser_UserIdAndSubject(userId, subject);
                chapterResults = chapterResults.stream()
                    .filter(cr -> cr.getCreatedAt() != null
                            && !cr.getCreatedAt().isBefore(startDate)
                            && !cr.getCreatedAt().isAfter(endDate))
                    .filter(cr -> cr.getChapter() != null)
                    .collect(Collectors.toList());
 
                Map<String, Double> chapterAverages = chapterResults.stream()
                    .collect(Collectors.groupingBy(
                        ChapterWiseResult::getChapter,
                        Collectors.averagingDouble(cr -> cr.getPercentage() != null ? cr.getPercentage().doubleValue() : 0.0)
                    ));
 
                details = chapterAverages.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
 
            } catch (Exception ex) {
                // fallback
            }
        }
 
        Map<String, Object> result = new HashMap<>();
        result.put("bars", bars);
        result.put("aiInsights", aiInsights);
        result.put("details", details);
        return result;
    }
 
    @Override
    public Map<String, Object> getIndividualChapterwiseAnalytics(Long userId, String subject, LocalDateTime startDate, LocalDateTime endDate) {
        List<ChapterWisePerformanceDTO> chapterStats =
            subjectWisePerformanceBarRepository.findChapterWisePerformanceByUserAndDifficultyAndSubject(
                userId, subject, startDate, endDate
            );
 
        List<Map<String, Object>> bars = chapterStats.stream()
            .map(dto -> {
                Map<String, Object> map = new HashMap<>();
                map.put("chapter", dto.getChapter());
                map.put("percentage", dto.getAveragePercentage() != null ? Math.round(dto.getAveragePercentage()) : 0);
                return map;
            })
            .collect(Collectors.toList());
 
        Map<String, Object> result = new HashMap<>();
        result.put("bars", bars);
        return result;
    }
}
