package com.brihathi.Multi_Tenant.controller;
 
import com.brihathi.Multi_Tenant.dto.FeedbackRequest;
import com.brihathi.Multi_Tenant.dto.FeedbackResponse;
import com.brihathi.Multi_Tenant.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
 
    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
 
    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        try {
            FeedbackResponse response = feedbackService.saveFeedback(feedbackRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
 