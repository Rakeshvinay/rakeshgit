package com.brihathi.Multi_Tenant.dto;
 
import com.brihathi.Multi_Tenant.entity.ExamQuestionsReport;
import com.brihathi.Multi_Tenant.enums.ReportValidated;
 
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
 
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamQuestionsReportRequestDTO {
    private Long userId;
    @JsonProperty("qId")
    private String qId;
    private Long tenantId;
    private String questionText;
    private String answeredOption;
    private String correctAnswerOption;
    private String report;
    private String answerOption1;
    private String answerOption2;
    private String answerOption3;
    private String answerOption4;
    private String actionsTaken;
    private ReportValidated isValidated;
}
