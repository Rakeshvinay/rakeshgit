package com.brihathi.Multi_Tenant.controller;
 
import com.brihathi.Multi_Tenant.dto.PreviousTestDTO;
import com.brihathi.Multi_Tenant.service.PreviousTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
 
@RestController
@RequestMapping("/api/previous-tests")
 
public class PreviousTestController {
    @Autowired
    private PreviousTestService previousTestService;
 
    @GetMapping("/{userId}")
    public List<PreviousTestDTO> getPreviousTests(@PathVariable long userId) {
        return previousTestService.getPreviousTests(userId);
    }
   
}
 
 