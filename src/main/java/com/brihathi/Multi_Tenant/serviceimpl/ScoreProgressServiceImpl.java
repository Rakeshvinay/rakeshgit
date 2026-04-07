package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.entity.ExamResult;
import com.brihathi.Multi_Tenant.entity.YourScoreProgress;
import com.brihathi.Multi_Tenant.repository.ExamRepository;
import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
import com.brihathi.Multi_Tenant.repository.YourScoreProgressRepository;
import com.brihathi.Multi_Tenant.service.ScoreProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
 
@Service
public class ScoreProgressServiceImpl implements ScoreProgressService {
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamResultRepository examResultRepository;
    @Autowired
    private YourScoreProgressRepository yourScoreProgressRepository;
 
    @Override
    @Transactional
    public void createScoreProgress(Long examId, Long userId) {
        Exam exam = examRepository.findByExamIdAndUser_UserId(examId, userId)
                .orElseThrow(() -> new RuntimeException("Exam not found for userId=" + userId + ", examId=" + examId));

        // Fetch only this user's results for this exam
        List<ExamResult> results = examResultRepository.findByExamIdAndUserId(examId, userId);
        if (results.isEmpty()) throw new RuntimeException("No exam results found for userId=" + userId + ", examId=" + examId);
        LocalDate date = exam.getEndDate() != null ? exam.getEndDate().toLocalDate() : LocalDate.now();
        // Group by subject
        Map<String, List<ExamResult>> resultsBySubject = results.stream()
            .collect(Collectors.groupingBy(ExamResult::getSubject));
        for (Map.Entry<String, List<ExamResult>> entry : resultsBySubject.entrySet()) {
            String subject = entry.getKey();
            List<ExamResult> subjectResults = entry.getValue();
            int totalMarks = 0, count = 0;
            for (ExamResult result : subjectResults) {
                String validate = result.getValidateAnswer();
                if (validate != null && "correct".equalsIgnoreCase(validate)) {
                    totalMarks += 4;
                } else if (validate != null && "wrong".equalsIgnoreCase(validate)) {
                    totalMarks -= 1;
                }
                count++;
            }
            int avgMarks = count > 0 ? Math.round((float) totalMarks / count) : 0;
            YourScoreProgress progress = YourScoreProgress.builder()
                .uuid(UUID.randomUUID())
                .tenantId(exam.getTenantId())
                .user(exam.getUser())
                .subject(subject)
                .avgMarks(avgMarks)
                .date(date)
                .build();
            yourScoreProgressRepository.save(progress);
        }
        // System.out.println("ScoreProgress created for userId=" + userId + ", examId=" + examId);
    }
}
