package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.dto.SwotResponseDTO;

public interface ScheduledOverallSwotService {
    SwotResponseDTO viewSwotDetails(Long userId, String subdomain);
    
} 
