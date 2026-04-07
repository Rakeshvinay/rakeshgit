package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.dto.ChapterPerformanceAnalyticsDTO;
import com.brihathi.Multi_Tenant.dto.ChapterAnalyticsResponseDTO;
import java.util.List;

public interface ScheduledChapterAnalyticsService {
    ChapterAnalyticsResponseDTO getChapterPerformance(String subdomain,
        String branch,
        String batch,
        String subject);

}
