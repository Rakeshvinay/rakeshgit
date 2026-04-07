package com.brihathi.Multi_Tenant.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.ZonedDateTime;

    @Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreProgressDTO {
    private String testName;
    private Integer score;
    private ZonedDateTime examDate;
}


