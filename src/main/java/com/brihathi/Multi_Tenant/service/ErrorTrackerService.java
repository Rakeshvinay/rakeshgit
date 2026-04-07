package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.ErrorTrackerResponseDTO;
 
import java.util.List;
 
public interface ErrorTrackerService {
    /**
     * Sync the error_tracker table for one exam:
     *   • add/update every wrong question
     *   • remove any question just answered correctly
     */
    void syncErrors(Long userId, Long examId);
    List<ErrorTrackerResponseDTO> getErrors(Long userId);
}
 
 
