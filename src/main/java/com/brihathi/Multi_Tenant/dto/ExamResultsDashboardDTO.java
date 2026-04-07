package com.brihathi.Multi_Tenant.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ExamResultsDashboardDTO {

    private String subject;
    private String branch;
    private String batch;
    private Integer maxMarks;

    private Double averageScore;
    private Integer highestScore;
    private Double passPercentage;
    private Integer totalStudents;
    private Integer presentStudents;
private Integer absentStudents;


    private List<StudentResultDTO> studentResults;
}
