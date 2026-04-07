package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.entity.TimeAnalysis;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.ExamResult;
import com.brihathi.Multi_Tenant.repository.TimeAnalysisRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.brihathi.Multi_Tenant.repository.ExamRepository;
import com.brihathi.Multi_Tenant.entity.Exam;

import com.brihathi.Multi_Tenant.repository.ExamResultRepository;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimeAnalysisService {

    @Autowired
    private TimeAnalysisRepository timeAnalysisRepository;
    
    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamResultRepository examResultRepository;

    public void aggregateAndInsertTimeAnalysis(Long examId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Exam exam = examRepository.findById(examId).orElseThrow();

        List<ExamResult> results = examResultRepository.findByExamId(examId);

        // Group by subject, calculate avg time per subject
        Map<String, List<ExamResult>> bySubject = results.stream()
            .collect(Collectors.groupingBy(ExamResult::getSubject));
        for (Map.Entry<String, List<ExamResult>> entry : bySubject.entrySet()) {
            String subject = entry.getKey();
            List<ExamResult> subjectResults = entry.getValue();
            int totalSeconds = subjectResults.stream()
                .mapToInt(er -> er.getDuration() != null ? (int) er.getDuration().getSeconds() : 0)
                .sum();
            int avgTime = subjectResults.isEmpty() ? 0 : totalSeconds / subjectResults.size();

            TimeAnalysis ta = TimeAnalysis.builder()
                .uuid(UUID.randomUUID())
                .user(user)
                .tenantId(exam.getTenantId())
                .subject(subject)
                .avgTime(avgTime)
                .build();
            timeAnalysisRepository.save(ta);
        }
        // System.out.println("TimeAnalysis created for userId=" + userId + ", examId=" + examId);
    }
}
