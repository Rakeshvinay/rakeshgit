package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.PreviousTestDTO;
import java.util.List;
 
 
 
 
public interface PreviousTestService {
    List<PreviousTestDTO> getPreviousTests(long userId);
   
}