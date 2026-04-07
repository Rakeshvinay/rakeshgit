package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class UpdateQuestionRequestDTO {

    private String questionId;
    private Boolean visited;
    private Boolean answered;
    private Boolean markedForReview;
    private Integer duration;     // seconds
    private String answerOption;  // A / B / C / D
}
