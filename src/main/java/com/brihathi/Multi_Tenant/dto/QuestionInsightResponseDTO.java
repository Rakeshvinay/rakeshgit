package com.brihathi.Multi_Tenant.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionInsightResponseDTO {

    private List<QuestionInsightDTO> mostWrong;
    private List<QuestionInsightDTO> mostSkipped;
    private List<QuestionInsightDTO> mostTimeConsuming;
}
