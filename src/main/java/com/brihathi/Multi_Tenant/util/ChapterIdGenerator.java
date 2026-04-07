package com.brihathi.Multi_Tenant.util;

import com.brihathi.Multi_Tenant.enums.Subject;
import org.springframework.stereotype.Component;

@Component
public class ChapterIdGenerator {
    
    public String generateChapterId(Subject subject, String chapter) {
        if (subject == null || chapter == null || chapter.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject and chapter cannot be null or empty");
        }
        
        // Get the subject code (e.g., "PHY" for Physics)
        String subjectPrefix = subject.getCode();
        
        // Clean and format the chapter name
        String formattedChapter = chapter.trim()
                .replaceAll("\\s+", "-")  // Replace spaces with hyphens
                .replaceAll("[^a-zA-Z0-9-]", "")  // Remove special characters
                .toUpperCase();
        
        // Combine to form the chapter ID
        return subjectPrefix + "-" + formattedChapter;
    }
} 