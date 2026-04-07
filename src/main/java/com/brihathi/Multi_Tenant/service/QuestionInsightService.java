package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.dto.QuestionInsightResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface QuestionInsightService {

    QuestionInsightResponseDTO getInsights(
            String branch,
            String batch,
            String subject,
            HttpServletRequest request
    );
}
