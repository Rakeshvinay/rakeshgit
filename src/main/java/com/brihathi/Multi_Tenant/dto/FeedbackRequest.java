package com.brihathi.Multi_Tenant.dto;
 
import com.brihathi.Multi_Tenant.enums.FeedbackType;
import com.brihathi.Multi_Tenant.enums.FeedbackReviewType;
 
public class FeedbackRequest {
    private Long userId; // nullable for APP feedback
    private FeedbackType feedbackType;
    private String name;
    private Long phone;
    private String feedback;
    private FeedbackReviewType review;
 
    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public FeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getPhone() { return phone; }
    public void setPhone(Long phone) { this.phone = phone; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public FeedbackReviewType getReview() { return review; }
    public void setReview(FeedbackReviewType review) { this.review = review; }
}
 