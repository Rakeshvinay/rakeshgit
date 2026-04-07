// package com.brihathi.Multi_Tenant.service;

// import com.brihathi.Multi_Tenant.entity.ScheduledTimeAnalysis;
// import com.brihathi.Multi_Tenant.entity.User;
// import com.brihathi.Multi_Tenant.entity.ExamResult;
// import com.brihathi.Multi_Tenant.repository.ScheduledTimeAnalysisRepository;
// import com.brihathi.Multi_Tenant.repository.UserRepository;
// import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class ScheduledTimeAnalysisService {

//     @Autowired
//     private ScheduledTimeAnalysisRepository timeAnalysisRepository;
//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private ExamResultRepository examResultRepository;

//     public void aggregateAndInsertTimeAnalysis(Long examId, Long userId) {
//         User user = userRepository.findById(userId).orElseThrow();
//         List<ExamResult> results = examResultRepository.findByExamId(examId);

//         // Group by subject, calculate avg time per subject
//         Map<String, List<ExamResult>> bySubject = results.stream()
//             .collect(Collectors.groupingBy(ExamResult::getSubject));
//         for (Map.Entry<String, List<ExamResult>> entry : bySubject.entrySet()) {
//             String subject = entry.getKey();
//             List<ExamResult> subjectResults = entry.getValue();
//             int totalSeconds = subjectResults.stream()
//                 .mapToInt(er -> er.getDuration() != null ? (int) er.getDuration().getSeconds() : 0)
//                 .sum();
//             int avgTime = subjectResults.isEmpty() ? 0 : totalSeconds / subjectResults.size();

//             ScheduledTimeAnalysis ta = ScheduledTimeAnalysis.builder()
//                 .uuid(UUID.randomUUID())
//                 .userId(userId)
//                 .subject(subject)
//                 .avgTime(avgTime)
//                 .build();
//             timeAnalysisRepository.save(ta);
//         }
//         // System.out.println("TimeAnalysis created for userId=" + userId + ", examId=" + examId);
//     }
// }
package com.brihathi.Multi_Tenant.service;

public interface ScheduledTimeAnalysisService {

    void aggregateAndInsertTimeAnalysis(Long eduScheduledExamId, Long userId);
}
