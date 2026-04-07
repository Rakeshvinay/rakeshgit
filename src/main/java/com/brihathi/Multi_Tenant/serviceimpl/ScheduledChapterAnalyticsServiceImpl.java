package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.dto.ChapterPerformanceAnalyticsDTO;
import com.brihathi.Multi_Tenant.dto.ChapterAnalyticsResponseDTO;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.repository.ScheduledChapterAnalyticsRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.service.ScheduledChapterAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledChapterAnalyticsServiceImpl implements ScheduledChapterAnalyticsService {

    private final ScheduledChapterAnalyticsRepository repository;
    private final TenantRepository tenantRepository;

    @Override
public ChapterAnalyticsResponseDTO getChapterPerformance(String subdomain,
                                                         String branch,
                                                         String batch,
                                                         String subject) {

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    List<ChapterPerformanceAnalyticsDTO> chapters =
            repository.getChapterPerformance(
                    tenant.getTenantId(), branch, batch, subject
            );

    // 🔥 Top 3
    List<ChapterPerformanceAnalyticsDTO> top3 = chapters.stream()
            .sorted(Comparator.comparingDouble(ChapterPerformanceAnalyticsDTO::getAveragePercentage).reversed())
            .limit(3)
            .toList();

    // ⚠️ Bottom 2
    List<ChapterPerformanceAnalyticsDTO> bottom2 = chapters.stream()
            .sorted(Comparator.comparingDouble(ChapterPerformanceAnalyticsDTO::getAveragePercentage))
            .limit(2)
            .toList();

    return new ChapterAnalyticsResponseDTO(chapters, top3, bottom2);
}

}
