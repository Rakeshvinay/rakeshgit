// package com.brihathi.Multi_Tenant.service;

// public interface ScheduledExamResultSummaryService {
//     void createExamResultSummary(Long examId, Long userId);
//     void createChapterWiseResults(Long examId, Long userId);
// } 

package com.brihathi.Multi_Tenant.service;

public interface ScheduledExamResultSummaryService {

    void createExamResultSummary(Long eduScheduledExamId, Long userId);

    void createChapterWiseResults(Long eduScheduledExamId, Long userId);
}
