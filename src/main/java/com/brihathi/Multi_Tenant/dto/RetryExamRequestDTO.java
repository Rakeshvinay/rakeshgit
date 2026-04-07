package com.brihathi.Multi_Tenant.dto;

import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.entity.QuestionTenant;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RetryExamRequestDTO {
    private Long userId;
    private Subject subject;
    private List<String> chapterIds;
    private Difficulty difficulty;
    private String grade;
    private List<QuestionPublic> questionsPublic;
    private List<QuestionTenant> questionTenant;
    
    @JsonProperty("questions")
    public void setQuestions(List<Object> questions) {
        if (questions != null) {
            this.questionsPublic = new ArrayList<>();
            this.questionTenant = new ArrayList<>();
            com.fasterxml.jackson.databind.ObjectMapper mapper = 
                new com.fasterxml.jackson.databind.ObjectMapper();
            
            for (Object q : questions) {
                if (q instanceof QuestionPublic) {
                    this.questionsPublic.add((QuestionPublic) q);
                } else if (q instanceof QuestionTenant) {
                    this.questionTenant.add((QuestionTenant) q);
                } else {
                    // Try to deserialize as Map and convert
                    try {
                        String json = mapper.writeValueAsString(q);
                        // Try QuestionPublic first
                        try {
                            QuestionPublic qp = mapper.readValue(json, QuestionPublic.class);
                            this.questionsPublic.add(qp);
                        } catch (Exception e) {
                            QuestionTenant qt = mapper.readValue(json, QuestionTenant.class);
                            this.questionTenant.add(qt);
                        }
                    } catch (Exception e) {
                        // Ignore if can't deserialize
                    }
                }
            }
        }
    }
}
