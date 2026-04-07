package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import lombok.Data;

@Data
public class CreateNormalExamRequest {

    // // ALL | SUBJECT | CHAPTER
    // private Subject examType;

    // Required for SUBJECT & CHAPTER
    private Subject subject;

    // Required only for CHAPTER
    private String chapterId;

    private Difficulty difficulty;
    private String grade;
}
