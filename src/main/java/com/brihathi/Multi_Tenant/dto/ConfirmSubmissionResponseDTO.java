package com.brihathi.Multi_Tenant.dto;

import lombok.Data;
import java.time.LocalDateTime;


import java.util.List;

@Data
public class ConfirmSubmissionResponseDTO {

    private String message;
    private Long examId;

    private Integer totalMarks;
    private Integer resultsSavedCount;

    private LocalDateTime submittedDateTime;


    private List<ConfirmChapterAnalysisDTO> chapters;
}
