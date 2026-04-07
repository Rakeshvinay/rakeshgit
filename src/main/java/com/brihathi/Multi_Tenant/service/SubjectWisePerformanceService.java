package com.brihathi.Multi_Tenant.service;

import java.util.Map;
import java.time.LocalDateTime;
 
public interface SubjectWisePerformanceService {
    void aggregateAndInsertSubjectWisePerformance(Long examId, Long userId);
    Map<String, Object> getIndividualSubjectwiseAnalytics(Long userId, String subject, LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getIndividualChapterwiseAnalytics(Long userId, String subject, LocalDateTime startDate, LocalDateTime endDate);
}
