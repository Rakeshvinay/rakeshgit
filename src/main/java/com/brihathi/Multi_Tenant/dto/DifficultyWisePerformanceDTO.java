package com.brihathi.Multi_Tenant.dto;

public interface DifficultyWisePerformanceDTO {
    String getDifficultyLevel();
    Double getAvgCorrectPercentage();
    Double getAvgWrongPercentage();
    Double getAvgUnattemptedPercentage();
}
