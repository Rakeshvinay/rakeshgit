package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.ScheduledTimeAnalysisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduledTimeAnalysisServiceImpl
        implements ScheduledTimeAnalysisService {

    @Autowired
    private ScheduledExamRepository scheduledExamRepository;

    @Autowired
    private ScheduledExamResultRepository scheduledExamResultRepository;

    @Autowired
    private ScheduledTimeAnalysisRepository timeAnalysisRepository;

    /* =====================================================
       TIME ANALYSIS (SCHEDULED)
       ===================================================== */

       @Override
       @Transactional
       public void aggregateAndInsertTimeAnalysis(Long eduScheduledExamId, Long userId) {
       
           // 1️⃣ Fetch student's scheduled exam (UNIQUE)
           ScheduledExam scheduledExam = scheduledExamRepository
                   .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
                   .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));
       
           Long scheduledExamId = scheduledExam.getScheduledExamId();
       
           // 2️⃣ Fetch raw results
           List<ScheduledExamResult> results =
                   scheduledExamResultRepository
                           .findByScheduledExamIdAndUserId(
                                   scheduledExamId, userId
                           );
       
           if (results == null || results.isEmpty()) return;
       
           // 3️⃣ Group by subject
           Map<String, List<ScheduledExamResult>> bySubject =
                   results.stream()
                           .collect(Collectors.groupingBy(
                                   ScheduledExamResult::getSubject
                           ));
       
           // 4️⃣ Avg time per subject
           for (Map.Entry<String, List<ScheduledExamResult>> entry : bySubject.entrySet()) {
       
               String subject = entry.getKey();
               List<ScheduledExamResult> subjectResults = entry.getValue();
       
               // Idempotency
               boolean exists =
                       timeAnalysisRepository
                               .findByScheduledExamIdAndUserIdAndSubject(
                                       scheduledExamId, userId, subject
                               ).isPresent();
       
               if (exists) continue;
       
               int totalSeconds =
                       subjectResults.stream()
                               .mapToInt(r ->
                                       r.getDuration() != null
                                               ? (int) r.getDuration().getSeconds()
                                               : 0
                               )
                               .sum();
       
               int avgTime = totalSeconds / subjectResults.size();
       
               // 5️⃣ Save
               ScheduledTimeAnalysis analysis =
                       ScheduledTimeAnalysis.builder()
                               .uuid(UUID.randomUUID())
                               .scheduledExamId(scheduledExamId)
                               .eduScheduledExamId(eduScheduledExamId)
                               .userId(userId)
                               .tenantId(scheduledExam.getTenantId())
                               .educatorId(scheduledExam.getEducatorId())
                               .branchId(scheduledExam.getBranchId())
                               .batchId(scheduledExam.getBatchId())
                               .subject(subject)
                               .avgTime(avgTime)
                               .build();
       
               timeAnalysisRepository.save(analysis);
           }
       }
       
}
