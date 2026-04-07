package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.Chapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterCreateResponse {
    private String message;
    private Chapter chapter;
} 