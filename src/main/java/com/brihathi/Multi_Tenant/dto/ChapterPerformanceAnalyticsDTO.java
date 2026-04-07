package com.brihathi.Multi_Tenant.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterPerformanceAnalyticsDTO {

    private String chapterId;
    private String chapterName;
    private String subject;
    private Double averagePercentage;
}
