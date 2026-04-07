package com.brihathi.Multi_Tenant.dto;

import lombok.Data;

@Data
public class EndExamSummaryDTO {

    private Long userId;
    private Long examId;

    private int totalQuestions;

    private int answered;
    private int notAnswered;

    private int markedForReview;
    private int answeredAndMarkedForReview;

    private int notVisited;
    private int visitedAndNotAnswered;

    private int maxPossibleMarks;

    private long totalTimeSpentSeconds;
    private String totalTimeSpent;

    private String message;
}
