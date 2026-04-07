package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.ExamResultsSummary;
import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.ExamResult;
import com.brihathi.Multi_Tenant.entity.ChapterWiseResult;
import com.brihathi.Multi_Tenant.entity.Chapter;
 
import com.brihathi.Multi_Tenant.repository.ChapterRepository;
import com.brihathi.Multi_Tenant.repository.ExamResultsSummaryRepository;
import com.brihathi.Multi_Tenant.repository.ExamRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
import com.brihathi.Multi_Tenant.repository.ChapterWiseResultRepository;
 
import com.brihathi.Multi_Tenant.service.ExamResultSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Map;
import java.lang.StringBuilder;
import com.brihathi.Multi_Tenant.service.DifficultyWisePerformanceService;

@Service
public class ExamResultSummaryServiceImpl implements ExamResultSummaryService {
    private static final Logger logger = LoggerFactory.getLogger(ExamResultSummaryServiceImpl.class);

    @Autowired
    private ExamResultsSummaryRepository summaryRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamResultRepository examResultRepository;
    @Autowired
    private ChapterWiseResultRepository chapterWiseResultRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private DifficultyWisePerformanceService difficultyWisePerformanceService;

    @Override
    @Transactional
    public void createExamResultSummary(Long examId, Long userId) {
        logger.info("createExamResultSummary start: examId={}, userId={}", examId, userId);

        // Idempotency: check if summary already exists
        if (summaryRepository.findByUser_UserIdAndExam_ExamId(userId, examId).isPresent()) {
            logger.warn("ExamResultsSummary already exists; skipping insert: examId={}, userId={}", examId, userId);
            return;
        }
 
        // Fetch User and Exam
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (userOpt.isEmpty() || examOpt.isEmpty()) {
            logger.warn("Cannot create summary; user or exam not found: examId={}, userFound={}, examFound={}",
                    examId, userOpt.isPresent(), examOpt.isPresent());
            return;
        }
        User user = userOpt.get();
        Exam exam = examOpt.get();
        logger.info("Loaded exam: examId={}, examTenantId={}, examUserId={}", exam.getExamId(), exam.getTenantId(),
                (exam.getUser() != null ? exam.getUser().getUserId() : null));
 
        // Fetch exam results ONLY from DB
        List<ExamResult> examResults = examResultRepository.findByExamId(examId);
        if (examResults == null || examResults.isEmpty()) {
            logger.warn("No exam_results rows found after retries; skipping summary insert: examId={}, userId={}", examId, userId);
            return;
        }

        long distinctUsers = examResults.stream().map(ExamResult::getUserId).distinct().count();
        Long anyTenantId = examResults.stream().map(ExamResult::getTenantId).filter(java.util.Objects::nonNull).findFirst().orElse(null);
        logger.info("ExamResults fetched: examId={}, rows={}, distinctUserIds={}, sampleTenantId={}",
                examId, examResults.size(), distinctUsers, anyTenantId);
 
        // Aggregate statistics
        int totalQuestions = examResults.size();
        int totalAnswered = (int) examResults.stream().filter(r -> Boolean.TRUE.equals(r.getAnswered())).count();
        int notAnswered = totalQuestions - totalAnswered;
        int markedForReview = (int) examResults.stream().filter(r -> Boolean.TRUE.equals(r.getMarkedForReview())).count();
        int answeredAndMarkedForReview = (int) examResults.stream().filter(r -> Boolean.TRUE.equals(r.getAnswered()) && Boolean.TRUE.equals(r.getMarkedForReview())).count();
        int notVisited = (int) examResults.stream().filter(r -> !Boolean.TRUE.equals(r.getVisited())).count();
 
        // Count correct and wrong answers
        int correctCount = (int) examResults.stream()
                .filter(r -> r.getValidateAnswer() != null && "correct".equalsIgnoreCase(r.getValidateAnswer()))
                .count();
        int wrongCount = (int) examResults.stream()
                .filter(r -> r.getValidateAnswer() != null && "wrong".equalsIgnoreCase(r.getValidateAnswer()))
                .count();

        logger.info("Summary computed: examId={}, userId={}, totalQ={}, answered={}, notAnswered={}, mfr={}, ans+mfr={}, notVisited={}, correct={}, wrong={}",
                examId, userId, totalQuestions, totalAnswered, notAnswered, markedForReview, answeredAndMarkedForReview,
                notVisited, correctCount, wrongCount);
 
        // Exam examEntity=examRepository.findByExamIdAndUser_UserId(examId, userId).orElse(null);
        // String difficultyLevel=examEntity!=null && examEntity.getDifficulty()!= null? examEntity.getDifficulty().name() : null;
        String difficultyLevel =
    exam.getDifficulty() != null ? exam.getDifficulty().name() : null;

        // Build and save summary
        ExamResultsSummary summary = ExamResultsSummary.builder()
                .user(user)
                .exam(exam)
                .tenantId(exam.getTenantId())
                .difficultyLevel(difficultyLevel)
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
        logger.info("ExamResultsSummary saved: examId={}, userId={}, tenantId={}", examId, userId, exam.getTenantId());
       

	difficultyWisePerformanceService.aggregateAndInsertDifficultyWisePerformance(userId, examId);
        logger.info("createExamResultSummary done: examId={}, userId={}", examId, userId);
    }
 
    private String generateChapterSummary(int marks, int totalQuestions, int correct, int duration, BigDecimal percentage) {
        StringBuilder summary = new StringBuilder();
       
        // Add performance level
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
 
        // Add score details
        summary.append(String.format("Scored %d out of %d possible marks. ", marks, totalQuestions * 4));
       
        // Add accuracy details
        int accuracy = totalQuestions > 0 ? (correct * 100) / totalQuestions : 0;
        summary.append(String.format("Accuracy: %d%%. ", accuracy));
       
        // Add time management feedback
        int avgTimePerQuestion = totalQuestions > 0 ? duration / totalQuestions : 0;
        if (avgTimePerQuestion <= 60) {
            summary.append("Good time management. ");
        } else if (avgTimePerQuestion <= 90) {
            summary.append("Moderate time management. ");
        } else {
            summary.append("Time management needs improvement. ");
        }
 
        // Add recommendation
        if (percentage.doubleValue() < 60) {
            summary.append("Focus on understanding core concepts and practice more questions.");
        } else if (percentage.doubleValue() < 80) {
            summary.append("Keep practicing to improve accuracy and speed.");
        } else {
            summary.append("Maintain this performance level with regular practice.");
        }
 
        return summary.toString();
    }
 
    @Override
    @Transactional
    public void createChapterWiseResults(Long examId, Long userId) {
        logger.info("createChapterWiseResults start: examId={}, userId={}", examId, userId);
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (userOpt.isEmpty() || examOpt.isEmpty()) {
            logger.warn("Cannot create chapter-wise results; user or exam not found: examId={}, userFound={}, examFound={}",
                    examId, userOpt.isPresent(), examOpt.isPresent());
            return;
        }
        User user = userOpt.get();
        Exam exam = examOpt.get();
        List<ExamResult> examResults = examResultRepository.findByExamId(examId);
        if (examResults == null || examResults.isEmpty()) {
            logger.warn("No exam_results rows found; skipping chapter-wise insert: examId={}, userId={}", examId, userId);
            return;
        }
        logger.info("Chapter-wise input rows: examId={}, rows={}", examId, examResults.size());
        // Group by chapter
        Map<String, List<ExamResult>> resultsByChapter = examResults.stream()
            .collect(java.util.stream.Collectors.groupingBy(ExamResult::getChapter));
        logger.info("Chapter-wise groups: examId={}, groups={}", examId, resultsByChapter.size());
        for (Map.Entry<String, List<ExamResult>> entry : resultsByChapter.entrySet()) {
            String chapterName = entry.getKey();
            List<ExamResult> chapterResults = entry.getValue();
            // List<ChapterResult> chapterWiseResults = entry.getValue();
 
            // Get the first ChapterResult for this group
            ExamResult firstResult = chapterResults.get(0);
 
            // If you have a corresponding ChapterResult entity, get the values from it
            String chapterNameFromResult = firstResult.getChapter();
            String subject = firstResult.getSubject();
 
            Chapter chapterEntity = chapterRepository.findBySubjectAndChapter(subject, chapterNameFromResult).orElse(null);
            String chapterId = chapterEntity != null ? chapterEntity.getChapterId() : null;
 
            int marks = chapterResults.stream()
                .mapToInt(er -> {
                    if (er.getValidateAnswer() != null && "correct".equalsIgnoreCase(er.getValidateAnswer())) return 4;
                    if (er.getValidateAnswer() != null && "wrong".equalsIgnoreCase(er.getValidateAnswer())) return -1;
                    return 0;
                })
                .sum();
            int duration = chapterResults.stream()
                .map(er -> er.getDuration() != null ? (int) er.getDuration().getSeconds() : 0)
                .reduce(0, Integer::sum);
         
                int totalQuestions = chapterResults.size();
            int totalMarks = chapterResults.size()*4;
            int correct = (int) chapterResults.stream().filter(er -> er.getValidateAnswer() != null && "correct".equalsIgnoreCase(er.getValidateAnswer())).count();
            int percentage = totalQuestions > 0 ? (marks * 100) / totalMarks : 0;
  
          
            // Generate summary text
            String summary = generateChapterSummary(marks, totalQuestions, correct, duration,BigDecimal.valueOf(percentage));
           
            ChapterWiseResult cwr = ChapterWiseResult.builder()
                .id(UUID.randomUUID())
                .user(user)
                .tenantId(exam.getTenantId())
                .exam(exam)
                .chapterId(chapterId)
                .chapter(chapterNameFromResult)
                .subject(subject)
                .marks(marks)
                .duration(duration)
                .percentage(BigDecimal.valueOf(Math.max(percentage, 0)))
 
                .summary(summary)
                .difficultyLevel(exam.getDifficulty() != null ? exam.getDifficulty().name() : null)
                .build();
            chapterWiseResultRepository.save(cwr);
            logger.info("ChapterWiseResult saved: examId={}, userId={}, chapter={}, subject={}, totalQ={}, marks={}, percentage={}",
                    examId, userId, chapterName, subject, totalQuestions, marks, percentage);
        }
        logger.info("createChapterWiseResults done: examId={}, userId={}", examId, userId);
    }
}


