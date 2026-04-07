
package com.brihathi.Multi_Tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectWisePerformanceChapterNormalDTO {
    private ChapterDTO chapter;
    private double percentage;
    private double marks;
    private double totalMarks;
    private String timeSpent;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterDTO {
        private String subject;
        private String name;
    }
}
