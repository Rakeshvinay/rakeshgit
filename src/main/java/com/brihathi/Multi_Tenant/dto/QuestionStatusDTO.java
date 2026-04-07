package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class QuestionStatusDTO {

    private Long examId;
    private String qid;
    private String chapter;

    private Boolean visited;
    private Boolean answered;
    private Boolean markedForReview;

    private String answerOption;
    private String duration; // HH:mm:ss
}
