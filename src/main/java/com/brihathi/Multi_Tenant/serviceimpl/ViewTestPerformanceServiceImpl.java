package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.context.TenantContext;
import com.brihathi.Multi_Tenant.dto.ViewTestPerformanceDTO;
import com.brihathi.Multi_Tenant.repository.ViewTestPerformanceRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.service.ViewTestPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
 
    @Service
    public class ViewTestPerformanceServiceImpl implements ViewTestPerformanceService {
    
        @Autowired
        private ViewTestPerformanceRepository viewTestPerformanceRepository;
    
        @Autowired
        private TenantRepository tenantRepository;
        @Autowired
 
        private TenantContext tenantContext;

    
        @Override
    public List<ViewTestPerformanceDTO> getExamQuestionResults(Long userId, Long examId) {

        // 1️⃣ Get tenant subdomain from context
        String tenantSubdomain = TenantContext.getTenant();

        // 2️⃣ Resolve Tenant entity
        Tenant tenant = tenantRepository.findBySubdomain(tenantSubdomain)
                .orElse(null);

        // 3️⃣ Decide question source
        boolean usePublic = tenant == null
                || "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

        // 4️⃣ Route to correct repository method
        return usePublic
                ? viewTestPerformanceRepository
                        .findExamQuestionPublicResults(userId, examId)
                : viewTestPerformanceRepository
                        .findExamQuestionTenantResults(userId, examId);
    }
    }
    

 
 
 