package com.brihathi.Multi_Tenant.service;

public interface ExamResultSummaryService {
    void createExamResultSummary(Long examId, Long userId);
    void createChapterWiseResults(Long examId, Long userId);
} 