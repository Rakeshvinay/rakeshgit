 package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExamPreviewResponse {

    private String totalDuration;          // "03:00:00"
    private Integer totalMarks;

    private List<QuestionPreviewDTO> questions;
}
 
