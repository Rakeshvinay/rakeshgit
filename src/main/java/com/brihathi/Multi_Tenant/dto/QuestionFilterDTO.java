package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.Data;

@Data
public class QuestionFilterDTO {
    private Subject subject;
    private String chapter;
    private Difficulty difficulty;
    private String grade;
} 