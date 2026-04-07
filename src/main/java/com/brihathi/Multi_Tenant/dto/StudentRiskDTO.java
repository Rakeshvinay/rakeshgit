package com.brihathi.Multi_Tenant.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
@Data
@Builder
public class StudentRiskDTO {
 
    private Long userId;
    private String userName;
    private String enrollmentId;
    private String branch;
    private String batch;
 
    private Integer noOfScheduledExams;
    private Integer noOfExamsAttempted;
    private Integer noOfExamsUnattempted;
    private Double percentage;
 
    private Double avgMarks;     // for low score list
    private String summary;
 
    public StudentRiskDTO(
            Long userId,
            String userName,
            String enrollmentId,
            String branch,
            String batch,
            Integer scheduled,
            Integer attempted,
            Integer unattempted,
            Double percentage,
            Double avgMarks,
            String summary
    ) {
        this.userId = userId;
        this.userName = userName;
        this.enrollmentId = enrollmentId;
        this.branch = branch;
        this.batch = batch;
        this.noOfScheduledExams = scheduled;
        this.noOfExamsAttempted = attempted;
        this.noOfExamsUnattempted = unattempted;
        this.percentage = percentage;
        this.avgMarks = avgMarks;
        this.summary = summary;
    }
}
 
 