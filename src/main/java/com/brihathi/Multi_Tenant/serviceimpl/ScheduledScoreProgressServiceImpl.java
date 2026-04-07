// package com.brihathi.Multi_Tenant.serviceimpl;
 
// import com.brihathi.Multi_Tenant.entity.ScheduledExam;
// import com.brihathi.Multi_Tenant.entity.ScheduledExamResult;
// import com.brihathi.Multi_Tenant.entity.ScheduledYourScoreProgress;
// import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledExamResultRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledYourScoreProgressRepository;
// import com.brihathi.Multi_Tenant.service.ScheduledScoreProgressService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.UUID;
// import java.util.Map;
// import java.util.stream.Collectors;
 
// @Service
// public class ScheduledScoreProgressServiceImpl implements ScheduledScoreProgressService {
//     @Autowired
//     private ScheduledExamRepository scheduledExamRepository;
//     @Autowired
//     private ScheduledExamResultRepository scheduledExamResultRepository;
//     @Autowired
//     private ScheduledYourScoreProgressRepository scheduledYourScoreProgressRepository;
 
//     @Override
//     @Transactional
//     public void createScoreProgress(Long eduScheduledExamId, Long userId) {
//         ScheduledExam exam = scheduledExamRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId).orElse(null);
//         if (exam == null) throw new RuntimeException("Exam not found");
//         List<ScheduledExamResult> results = scheduledExamResultRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId);
//         if (results.isEmpty()) throw new RuntimeException("No exam results found");
//         LocalDate date = exam.getEndDate() != null ? exam.getEndDate().toLocalDate() : LocalDate.now();
//         // Group by subject
//         Map<String, List<ScheduledExamResult>> resultsBySubject = results.stream()
//             .collect(Collectors.groupingBy(ScheduledExamResult::getSubject));
//         for (Map.Entry<String, List<ScheduledExamResult>> entry : resultsBySubject.entrySet()) {
//             String subject = entry.getKey();
//             List<ScheduledExamResult> subjectResults = entry.getValue();
//             int totalMarks = 0, count = 0;
//             for (ScheduledExamResult result : subjectResults) {
//                 if ("correct".equals(result.getValidateAnswer())) totalMarks += 4;
//                 else if ("wrong".equals(result.getValidateAnswer())) totalMarks -= 1;
//                 count++;
//             }
//             int avgMarks = count > 0 ? totalMarks : 0;
//             ScheduledYourScoreProgress progress = ScheduledYourScoreProgress.builder()
//                 .uuid(UUID.randomUUID())
//                 .eduScheduledExamId(eduScheduledExamId)
//                 .batchId(Long.parseLong(exam.getBatchId()))
//                 .branchId(Long.parseLong(exam.getBranchId()))
//                 .tenantId(exam.getTenantId())
//                 .scheduledExamId(exam.getScheduledExamId())
//                 .educatorId(exam.getEducatorId())
//                 .userId(userId)
//                 .subject(subject)
//                 .avgMarks(avgMarks)
//                 .date(date)
//                 .build();
//                 scheduledYourScoreProgressRepository.save(progress);
//         }
//         System.out.println("ScoreProgress created for userId=" + userId + ", eduScheduledExamId=" + eduScheduledExamId);
//     }
// }


package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.ScheduledScoreProgressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduledScoreProgressServiceImpl
        implements ScheduledScoreProgressService {

    @Autowired
    private ScheduledExamRepository scheduledExamRepository;

    @Autowired
    private ScheduledExamResultRepository scheduledExamResultRepository;

    @Autowired
    private ScheduledYourScoreProgressRepository scoreProgressRepository;

    /* =====================================================
       SCORE PROGRESS (SCHEDULED)
       ===================================================== */

       @Override
       @Transactional
       public void createScoreProgress(Long eduScheduledExamId, Long userId) {
       
           ScheduledExam scheduledExam =
                   scheduledExamRepository
                           .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
                           .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));
       
           Long scheduledExamId = scheduledExam.getScheduledExamId();
       
           List<ScheduledExamResult> results =
                   scheduledExamResultRepository
                           .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId);
       
           if (results.isEmpty()) return;
       
           LocalDate date = scheduledExam.getEndDate() != null
                   ? scheduledExam.getEndDate().toLocalDate()
                   : LocalDate.now();
       
           Map<String, List<ScheduledExamResult>> resultsBySubject =
                   results.stream().collect(Collectors.groupingBy(ScheduledExamResult::getSubject));
       
           for (Map.Entry<String, List<ScheduledExamResult>> entry : resultsBySubject.entrySet()) {
       
               String subject = entry.getKey();
               List<ScheduledExamResult> subjectResults = entry.getValue();
       
               boolean exists = scoreProgressRepository
                       .findByScheduledExamIdAndUserIdAndSubject(scheduledExamId, userId, subject)
                       .isPresent();
       
               if (exists) continue;
       
               int totalMarks = 0;
        //        int count = subjectResults.size();
               int count = 0;
               for (ScheduledExamResult result : subjectResults) {
                String validate = result.getValidateAnswer();
                if (validate != null && "correct".equalsIgnoreCase(validate)) {
                    totalMarks += 4;
                } else if (validate != null && "wrong".equalsIgnoreCase(validate)) {
                    totalMarks -= 1;
                }
                count++;
            }
       
        //        for (ScheduledExamResult result : subjectResults) {
        //            if ("correct".equals(result.getValidateAnswer())) totalMarks += 4;
        //            else if ("wrong".equals(result.getValidateAnswer())) totalMarks -= 1;
        //        }
       
        //        int avgMarks = count > 0 ? totalMarks / count : 0;
        int avgMarks = count > 0 ? Math.round((float) totalMarks / count) : 0;

       
               ScheduledYourScoreProgress progress = ScheduledYourScoreProgress.builder()
                       .uuid(UUID.randomUUID())
                       .scheduledExamId(scheduledExamId)
                       .eduScheduledExamId(eduScheduledExamId)
                       .userId(userId)
                       .tenantId(scheduledExam.getTenantId())
                       .educatorId(scheduledExam.getEducatorId())
                       .branchId(scheduledExam.getBranchId())
                       .batchId(scheduledExam.getBatchId())
                       .subject(subject)
                       .avgMarks(avgMarks)
                       .date(date)
                       .build();
       
               scoreProgressRepository.save(progress);
           }
       }
       
}
