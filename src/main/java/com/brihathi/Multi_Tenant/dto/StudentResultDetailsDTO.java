package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.List;
import lombok.Builder;

@Data
@Builder
public class StudentResultDetailsDTO {

    private Integer rank;
    private String studentName;
    private String rollNo;
    private String branch;
    private String batch;
    private String subject;
    private Integer maxMarks;
    private Integer score;
    private Double percentage;
    private String studentAttendance;

    private ResultAnalyticsDTO analytics;
    private List<QuestionReviewDTO> questions;
}
