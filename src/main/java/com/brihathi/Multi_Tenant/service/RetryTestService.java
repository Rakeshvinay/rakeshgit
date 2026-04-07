package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;

import java.util.List;
import java.util.Map;

public interface RetryTestService {

    List<Map<String, Object>> getExamQuestionResults(Long userId, Long examId);

    Map<String, Object> createAndStartRetryExam(
            Long userId,
            Subject subject,
            List<String> chapterIds,
            Difficulty difficulty,
            String grade,
            List<Object> questions
    );
}
