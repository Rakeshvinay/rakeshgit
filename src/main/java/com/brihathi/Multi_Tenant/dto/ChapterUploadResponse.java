package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.Chapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterUploadResponse {
    private String message;
    private List<Chapter> chapters;
} 