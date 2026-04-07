package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.dto.FeedbackRequest;
import com.brihathi.Multi_Tenant.dto.FeedbackResponse;
import com.brihathi.Multi_Tenant.entity.Feedback;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.enums.FeedbackType;
import com.brihathi.Multi_Tenant.repository.FeedbackRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
 
    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }
 
    @Override
    public FeedbackResponse saveFeedback(FeedbackRequest request) {
        // Validation logic
        if (request.getFeedbackType() == FeedbackType.EXAM && request.getUserId() == null) {
            throw new IllegalArgumentException("User is required for EXAM feedback.");
        }
        // if (request.getFeedbackType() == FeedbackType.APP &&
        //         (request.getName() == null || request.getName().isBlank() || request.getPhone() == null)) {
        //     throw new IllegalArgumentException("Name and phone are required for APP feedback.");
        // }
 
        Feedback feedback = new Feedback();
        feedback.setFeedbackType(request.getFeedbackType());
        feedback.setName(request.getName());
        feedback.setPhone(request.getPhone());
        feedback.setFeedback(request.getFeedback());
        feedback.setReview(request.getReview());
 
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId()).orElse(null);
            feedback.setUser(user);
            if (user != null) {
                feedback.setName(user.getName());
                feedback.setPhone(user.getPhoneNumber());
            }
        }
 
        Feedback saved = feedbackRepository.save(feedback);
 
        FeedbackResponse response = new FeedbackResponse();
        response.setUuid(saved.getUuid());
        response.setUserId(saved.getUser() != null ? saved.getUser().getUserId() : null);
        response.setFeedbackType(saved.getFeedbackType());
        if (saved.getFeedbackType() == FeedbackType.EXAM && saved.getUser() != null) {
            response.setName(saved.getUser().getName());
            response.setPhone(saved.getUser().getPhoneNumber());
        } else {
            response.setName(saved.getName());
            response.setPhone(saved.getPhone());
        }
        response.setFeedback(saved.getFeedback());
        response.setReview(saved.getReview());
        return response;
    }
}
 
 
 