package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentResultDTO {
   
    private Integer rank;
    private Long scheduledExamId;   // ✅ ADD THIS
    private String studentName;
    private String rollNo;
    private String branch;
    private Integer score;
    private Double percentage;
    private String studentAttendance; 
    // 🔥 Constructor used by JPQL query
    public StudentResultDTO(
        
        Integer rank,
        Long scheduledExamId,
        String studentName,
        String rollNo,
        String branch,
        Integer score,
        Double percentage
) {
   
    this.rank = rank;
    this.scheduledExamId = scheduledExamId;
    this.studentName = studentName;
    this.rollNo = rollNo;
    this.branch = branch;
    this.score = score;
    this.percentage = percentage;
}
}
