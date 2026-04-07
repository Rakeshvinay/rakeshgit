package com.brihathi.Multi_Tenant.dto;
 
import com.brihathi.Multi_Tenant.enums.Subject;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
public class SubjectQuestionCountDTO {
    private Subject subject; // enum type, not String
    private Long count;
}
 