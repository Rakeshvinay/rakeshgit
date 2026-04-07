package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.FeedbackRequest;
import com.brihathi.Multi_Tenant.dto.FeedbackResponse;
 
public interface FeedbackService {
    FeedbackResponse saveFeedback(FeedbackRequest feedbackRequest);
}