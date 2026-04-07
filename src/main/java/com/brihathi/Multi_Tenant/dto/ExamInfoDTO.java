package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import lombok.Data;
import java.util.List;

@Data
public class ExamInfoDTO {
    private List<SubjectInfo> subjects;

    @Data
    public static class SubjectInfo {
        private Subject subject;
        private List<String> chapters;
        private List<Difficulty> difficulties;
        private List<String> grades;
        private List<TenantDTO> tenants;
    }
} 