package com.brihathi.Multi_Tenant.serviceimpl;


import com.brihathi.Multi_Tenant.dto.*;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.SubjectAnalyticsRepository;
import com.brihathi.Multi_Tenant.service.SubjectAnalyticsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;
@Service
@RequiredArgsConstructor
public class SubjectAnalyticsServiceImpl implements SubjectAnalyticsService {

    private final TenantRepository tenantRepository;
    private final SubjectAnalyticsRepository analyticsRepository;

    @Override
    public PerformanceDistributionResponseDTO getPerformanceDistribution(String subdomain, String branch, String batch) {
    
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    
        Long tenantId = tenant.getTenantId();
    
        Long totalStudents = analyticsRepository.getTotalStudents(tenantId, branch, batch);
        Double avgPercentage = analyticsRepository.getAveragePercentage(tenantId, branch, batch);
    
        if (avgPercentage == null) avgPercentage = 0.0;
    
        Map<String, String> labelMap = Map.of(
                "ABOVE_90", "Above 90%",
                "80_90", "80-90%",
                "70_80", "70-80%",
                "60_70", "60-70%",
                "50_60", "50-60%",
                "BELOW_50", "Below 50%"
        );
    
        Map<String, Long> counts = analyticsRepository.getPerformanceDistribution(
                tenantId, branch, batch
        ).stream().collect(Collectors.toMap(
                r -> (String) r[0],
                r -> (Long) r[1]
        ));
    
        List<String> order = List.of("ABOVE_90","80_90","70_80","60_70","50_60","BELOW_50");
    
        List<PerformanceDistributionDTO> distribution = order.stream().map(key -> {
            Long count = counts.getOrDefault(key, 0L);
            Double percent = totalStudents == 0 ? 0 : (count * 100.0) / totalStudents;
    
            return PerformanceDistributionDTO.builder()
                    .range(labelMap.get(key))
                    .studentCount(count)
                    .percentage(percent)
                    .build();
        }).toList();
    
        return PerformanceDistributionResponseDTO.builder()
                .totalStudents(totalStudents)
                .averagePercentage(avgPercentage)
                .distribution(distribution)
                .build();
    }
    



    @Override
    public List<SubjectPerformanceDTO> getSubjectPerformance(
            String subdomain, String branch, String batch) {

        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        List<Object[]> data = analyticsRepository.getSubjectPerformance(
                tenant.getTenantId(), branch, batch);

        List<SubjectPerformanceDTO> list = new ArrayList<>();

        for (Object[] row : data) {
            String subject = (String) row[0];
            Long count = (Long) row[1];
            Double avgMarks = row[2] == null ? 0 : ((Number) row[2]).doubleValue();
            Double avgPercent = row[3] == null ? 0 : ((Number) row[3]).doubleValue();

            list.add(SubjectPerformanceDTO.builder()
                    .subject(subject)
                    .studentCount(count)
                    .averageMarks(round(avgMarks))
                    .averagePercentage(round(avgPercent))
                    .performance(getPerformanceLabel(avgPercent))
                    .build());
        }

        return list;
    }

    private String getPerformanceLabel(double p) {
        if (p >= 80) return "Excellent";
        if (p >= 70) return "Good";
        if (p >= 50) return "Average";
        return "Needs Improvement";
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}



