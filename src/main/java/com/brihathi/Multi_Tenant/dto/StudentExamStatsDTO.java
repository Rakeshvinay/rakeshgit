package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
 @Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentExamStatsDTO {

    private Long attemptedExams;
    private Long unattemptedExams;
    private Double predictedScore;
    private Integer predictedRank;
}
 
