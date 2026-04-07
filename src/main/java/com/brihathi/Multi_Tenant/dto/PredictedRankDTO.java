package com.brihathi.Multi_Tenant.dto;

public interface PredictedRankDTO {
    Integer getPredictedScore();
    Integer getPredictedRank();
    String getNeedToImprove();
    String getGoodAt();
    Integer getNoOfExams();
}
