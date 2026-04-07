package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
}