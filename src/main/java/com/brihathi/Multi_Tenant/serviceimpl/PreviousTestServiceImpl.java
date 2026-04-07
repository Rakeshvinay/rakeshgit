package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.dto.PreviousTestDTO;
import com.brihathi.Multi_Tenant.repository.PreviousTestsRepository;
import com.brihathi.Multi_Tenant.service.PreviousTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
 
@Service
public class PreviousTestServiceImpl implements PreviousTestService {
 
    @Autowired
    private  PreviousTestsRepository prevtestsRepository;
 
   
 
    // @Override
    // public List<PreviousTestDTO> getPreviousTests(long userId) {
    //     return prevtestsRepository.findPreviousTestsByUserId(userId);
    // }
    @Override
public List<PreviousTestDTO> getPreviousTests(long userId) {
    return prevtestsRepository.findPreviousTestsByUserId(userId);
}

}
 
 