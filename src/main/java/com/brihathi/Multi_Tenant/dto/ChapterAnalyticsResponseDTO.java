package com.brihathi.Multi_Tenant.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterAnalyticsResponseDTO {

    private List<ChapterPerformanceAnalyticsDTO> chapters;
    private List<ChapterPerformanceAnalyticsDTO> topChapters;
    private List<ChapterPerformanceAnalyticsDTO> needAttentionChapters;
}
