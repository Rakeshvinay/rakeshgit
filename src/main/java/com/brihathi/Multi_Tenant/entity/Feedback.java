package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.brihathi.Multi_Tenant.enums.FeedbackType;
import com.brihathi.Multi_Tenant.enums.FeedbackReviewType;
 
import java.util.UUID;
 
@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "tenant_id")
    private Long tenantId;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 255)
    private FeedbackType feedbackType;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "review", length = 32)
    private FeedbackReviewType review;
 
    @Column(name = "name", length = 255)
    private String name;
 
    @Column(name = "phone")
    private Long phone;
 
    @Column(name = "feedback", nullable = false, columnDefinition = "TEXT")
    private String feedback;
}
 