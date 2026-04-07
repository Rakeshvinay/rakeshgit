package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class ConfirmChapterAnalysisDTO {

    private ConfirmChapterInfoDTO chapter;

    private String timeSpent;     // HH:mm:ss
    private double percentage;
    private String examType;

    private Integer marks;
    private Integer totalMarks;
    private String aiAnalysis;
}
