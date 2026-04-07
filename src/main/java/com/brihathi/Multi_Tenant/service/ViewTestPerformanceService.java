package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.dto.ViewTestPerformanceDTO;
 
import java.util.List;
 
public interface ViewTestPerformanceService {
    List<ViewTestPerformanceDTO> getExamQuestionResults(Long userId, Long examId);
}
