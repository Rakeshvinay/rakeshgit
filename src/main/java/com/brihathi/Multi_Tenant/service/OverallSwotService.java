
package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.dto.SwotNormalResponseDTO;

public interface OverallSwotService {
    SwotNormalResponseDTO generateOverallSwot(Long userId);
}
