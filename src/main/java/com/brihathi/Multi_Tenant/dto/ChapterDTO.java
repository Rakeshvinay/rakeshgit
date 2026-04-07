package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class ChapterDTO {
    private String chapterId;
    private String chapter;
    private String subject;
    private Double weightage;
    private Integer number_of_questions;
} 