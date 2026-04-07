package com.brihathi.Multi_Tenant.dto;

public interface SubjectWisePerformanceDTO {
    String getSubject();
    Double getTotalPercentage();
    Integer getCountRows();
    Double getAveragePercentage();
}
