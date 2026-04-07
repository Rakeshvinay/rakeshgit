package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.BatchProgressSummaryDTO;
import com.brihathi.Multi_Tenant.dto.BatchAttendanceResponseDTO;
import com.brihathi.Multi_Tenant.dto.StudentRiskDTO;
import com.brihathi.Multi_Tenant.dto.BatchAIInsightDTO;
import java.util.List;
import java.util.Map;
 
public interface BatchProgressService {
    List<BatchProgressSummaryDTO> getBatchProgress(String subdomain,String branch, String batch);
    public List<BatchAttendanceResponseDTO> getBatchAttendance( String subdomain,String branch, String batch);
    public Map<String, List<StudentRiskDTO>> getStudentRisk(String subdomain, String branch, String batch);
 
    public List<BatchAIInsightDTO> getBatchAIInsights(String subdomain, String branch, String batch);
}
 
 