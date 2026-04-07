package com.brihathi.Multi_Tenant.dto;
import lombok.*;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@Data
@Builder
public class BatchAttendanceResponseDTO {
    private String batch;
    private List<BatchExamTrendDTO> exams;
}