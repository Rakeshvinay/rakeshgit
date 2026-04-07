// // package com.brihathi.Neet.Swan.dto;

// // import lombok.Data;

// // import java.util.Date;
// // import java.util.List;
// // import java.util.Map;

// // @Data
// // public class SwotResponseDTO {
// //     private String examType;
// //     private List<SubjectWisePerformanceChapterDTO> chapters;
// //     private Map<String, SubjectSwot> swot;
// //     private String totalTimeSpent;
// //     private Date submittedDateTime;
// //     private int totalMarks;

// //     @Data
// //     public static class SubjectSwot {
// //         private String subject;
// //         private Strength strengths;
// //         private Weakness weaknesses;
// //         private Opportunity opportunities;
// //         private Threat threats;
// //     }

// //     @Data
// //     public static class Strength {
// //         private String subject;
// //         private List<String> topChapters;
// //         private List<String> messages;
// //     }

// //     @Data
// //     public static class Weakness {
// //         private String subject;
// //         private List<String> lowestChapters;
// //         private List<String> messages;
// //     }

// //     @Data
// //     public static class Opportunity {
// //         private String subject;
// //         private List<String> lowestChapters;
// //         private List<String> messages;
// //     }

// //     @Data
// //     public static class Threat {
// //         private String subject;
// //         private List<String> messages;
// //     }
// // }
// package com.brihathi.Neet.Swan.dto;

// import lombok.Data;

// import java.util.Date;
// import java.util.List;
// import java.util.Map;

// @Data
// public class SwotResponseDTO {
//     private String examType;
//     private List<SubjectWisePerformanceChapterDTO> chapters;
//     private Map<String, SubjectSwot> swot;
//     private String totalTimeSpent;
//     private Date submittedDateTime;
//     private int totalMarks;

//     @Data
//     public static class SubjectSwot {
//         private String subject;
//         private Strength strengths;
//         private Weakness weaknesses;
//         private Opportunity opportunities;
//         private Threat threats;
//     }

//     @Data
//     public static class Strength {
//         private String subject;
//         private List<String> topChapters;
//         private List<String> messages;
//     }

//     @Data
//     public static class Weakness {
//         private String subject;
//         private List<String> lowestChapters;
//         private List<String> messages;
//     }

//     @Data
//     public static class Opportunity {
//         private String subject;
//         private List<String> lowestChapters;
//         private List<String> messages;
//     }

//     @Data
//     public static class Threat {
//         private String subject;
//         private List<String> messages;
//     }
// }
// package com.brihathi.Multi_Tenant.dto;

// import lombok.Data;

// import java.util.Date;
// import java.util.List;
// import java.util.Map;

// @Data
// public class SwotResponseDTO {
//     private String examType;
//     private List<SubjectWisePerformanceChapterDTO> chapters;
//     private Map<String, SubjectSwot> swot;
//     private Map<String, AiInsightDTO> aiInsights; // ✅ Added for AI Insights
//     private String totalTimeSpent;
//     private Date submittedDateTime;
//     private int totalMarks;

//     @Data
//     public static class SubjectSwot {
//         private String subject;
//         private Strength strengths;
//         private Weakness weaknesses;
//         private Opportunity opportunities;
//         private Threat threats;
//     }

//     @Data
//     public static class Strength {
//         private String subject;
//         private List<String> topChapters;
//         private List<String> messages;
//     }

//     @Data
//     public static class Weakness {
//         private String subject;
//         private List<String> lowestChapters;
//         private List<String> messages;
//     }

//     @Data
//     public static class Opportunity {
//         private String subject;
//         private List<String> lowestChapters;
//         private List<String> messages;
//     }

//     @Data
//     public static class Threat {
//         private String subject;
//         private List<String> messages;
//     }

//     // ✅ NEW: Insight DTO for average subject-wise performance
//     @Data
//     public static class AiInsightDTO {
//         private String subject;
//         private double avgMarks;
//         private double avgPercentage;
//         private String avgTimeSpent;
//         private double avgNegativeMarks;
//         private List<String> messages;
//     }
// }
package com.brihathi.Multi_Tenant.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwotResponseDTO {

    private String examType;
    private List<ChapterPerformanceDTO> chapters;
    private Map<String, SubjectSwotDTO> swot;
    private Map<String, AiInsightDTO> aiInsights;
    private String totalTimeSpent;
    private LocalDateTime submittedDateTime;
    private double totalMarks;

    // ================= CHAPTER PERFORMANCE =================
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChapterPerformanceDTO {
        private ChapterSDTO chapter;   // 👈 THIS MATCHES YOUR SERVICE
        private double percentage;
        private double marks;
        private double totalMarks;
        private String timeSpent;
    }

    // ================= CHAPTER BASIC DTO =================
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChapterSDTO {
        private String subject;
        private String name;
    }

    // ================= SWOT =================
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectSwotDTO {
        private String subject;
        private StrengthDTO strengths;
        private WeaknessDTO weaknesses;
        private OpportunityDTO opportunities;
        private ThreatDTO threats;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrengthDTO {
        private String subject;
        private List<String> topChapters;
        private List<String> messages;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeaknessDTO {
        private String subject;
        private List<String> lowestChapters;
        private List<String> messages;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpportunityDTO {
        private String subject;
        private List<String> lowestChapters;
        private List<String> messages;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThreatDTO {
        private String subject;
        private List<String> messages;
    }

    // ================= AI INSIGHTS =================
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AiInsightDTO {
        private String subject;
        private double avgMarks;
        private double avgPercentage;
        private String avgTimeSpent;
        private double avgNegativeMarks;
        private List<String> messages;
    }
}
