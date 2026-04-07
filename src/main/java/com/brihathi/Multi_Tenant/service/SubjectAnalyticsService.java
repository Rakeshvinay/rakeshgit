package com.brihathi.Multi_Tenant.service;
import java.util.List;
import com.brihathi.Multi_Tenant.dto.PerformanceDistributionResponseDTO;
import com.brihathi.Multi_Tenant.dto.SubjectPerformanceDTO;

public interface SubjectAnalyticsService {
    PerformanceDistributionResponseDTO getPerformanceDistribution(String subdomain, String branch, String batch);

    List<SubjectPerformanceDTO> getSubjectPerformance(
        String subdomain, String branch, String batch);
}

