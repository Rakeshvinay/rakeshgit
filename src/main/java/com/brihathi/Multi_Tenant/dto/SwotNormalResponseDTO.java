package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class SwotNormalResponseDTO {
    private String examType;
    private List<SubjectWisePerformanceChapterNormalDTO> chapters;
    private Map<String, SubjectSwot> swot;
    private Map<String, AiInsightDTO> aiInsights; // ✅ Added for AI Insights
    private String totalTimeSpent;
    private Date submittedDateTime;
    private int totalMarks;

    @Data
    public static class SubjectSwot {
        private String subject;
        private Strength strengths;
        private Weakness weaknesses;
        private Opportunity opportunities;
        private Threat threats;
    }

    @Data
    public static class Strength {
        private String subject;
        private List<String> topChapters;
        private List<String> messages;
    }

    @Data
    public static class Weakness {
        private String subject;
        private List<String> lowestChapters;
        private List<String> messages;
    }

    @Data
    public static class Opportunity {
        private String subject;
        private List<String> lowestChapters;
        private List<String> messages;
    }

    @Data
    public static class Threat {
        private String subject;
        private List<String> messages;
    }

    // ✅ NEW: Insight DTO for average subject-wise performance
    @Data
    public static class AiInsightDTO {
        private String subject;
        private double avgMarks;
        private double avgPercentage;
        private String avgTimeSpent;
        private double avgNegativeMarks;
        private List<String> messages;
    }
}
