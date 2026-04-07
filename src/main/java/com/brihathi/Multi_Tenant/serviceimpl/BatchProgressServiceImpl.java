package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.dto.StudentRiskDTO;
import com.brihathi.Multi_Tenant.dto.BatchProgressSummaryDTO;
import com.brihathi.Multi_Tenant.dto.BatchAttendanceResponseDTO;
import com.brihathi.Multi_Tenant.dto.BatchExamTrendDTO;
import com.brihathi.Multi_Tenant.dto.BatchAIInsightDTO;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.repository.BatchProgressRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.service.BatchProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.sql.Timestamp;
import java.sql.Date;
 
@Service
@RequiredArgsConstructor
public class BatchProgressServiceImpl implements BatchProgressService {
 
    private final BatchProgressRepository repository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
 
 
    @Override
    public List<BatchProgressSummaryDTO> getBatchProgress(String subdomain,String branch, String batch) {
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
        .orElseThrow(() -> new RuntimeException("Tenant not found"));
 
Long tenantId = tenant.getTenantId();   // 🔥 IMPORTANT
 
 
// 🔥 VALIDATION BLOCK (MISSING IN YOUR CODE)
if (!userRepository.existsByTenantIdAndBranch(tenantId, branch)) {
throw new RuntimeException("Branch '" + branch + "' not found in this tenant");
}
 
if (batch != null &&
!userRepository.existsByTenantIdAndBranchAndBatch(tenantId, branch, batch)) {
throw new RuntimeException("Batch '" + batch + "' not found in this tenant");
}
 
        return repository.getBatchProgress(tenantId,branch, batch)
                .stream()
                .map(r -> BatchProgressSummaryDTO.builder()
                        .branch((String) r[0])
                        .batch((String) r[1])
                        .totalStudents(r[2] != null ? ((Number) r[2]).longValue() : 0)
                        .avgMarks(r[3] != null ? ((Number) r[3]).doubleValue() : 0)
                        .accuracy(r[4] != null ? ((Number) r[4]).doubleValue() : 0)
                        .testsConducted(r[5] != null ? ((Number) r[5]).longValue() : 0)
                        .build()
                ).collect(Collectors.toList());
    }
 
 
@Override
    public List<BatchAttendanceResponseDTO> getBatchAttendance( String subdomain,String branch, String batch) {
 
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
 
        Long tenantId = tenant.getTenantId();   // 🔥 IMPORTANT
 
       
    // 🔥 VALIDATION BLOCK (MISSING IN YOUR CODE)
    if (!userRepository.existsByTenantIdAndBranch(tenantId, branch)) {
        throw new RuntimeException("Branch '" + branch + "' not found in this tenant");
    }
 
    if (batch != null &&
        !userRepository.existsByTenantIdAndBranchAndBatch(tenantId, branch, batch)) {
        throw new RuntimeException("Batch '" + batch + "' not found in this tenant");
    }
 
        List<Object[]> rows = repository.getBatchExamAttendance(tenantId, branch, batch);
 
 
        Map<String, List<BatchExamTrendDTO>> batchMap = new LinkedHashMap<>();
 
        for (Object[] r : rows) {
 
            String batchName = (String) r[2];
 
            int totalScheduled = ((Number) r[5]).intValue();
            int totalAttempted = ((Number) r[6]).intValue();
            int unattempted = totalScheduled - totalAttempted;
 
            double percent = totalScheduled == 0 ? 0 :
                    (totalAttempted * 100.0) / totalScheduled;
 
 
                    Timestamp ts = (Timestamp) r[3];
                    LocalDateTime scheduledTime = ts != null ? ts.toLocalDateTime() : null;
                   
                    Date date = (Date) r[4];
                    LocalDate scheduledDate = date != null ? date.toLocalDate() : null;
                   
            BatchExamTrendDTO dto = BatchExamTrendDTO.builder()
                    .eduScheduledExamId(((Number) r[0]).longValue())
                    .branch((String) r[1])
                    .batch(batchName)
                    .scheduledTime(scheduledTime)
                    .scheduledDate(scheduledDate)
                    .noOfStudentsScheduled(totalScheduled)
                    .noOfStudentsAttempted(totalAttempted)
                    .noOfStudentsUnattempted(unattempted)
                    .percentage(percent)
                    .build();
 
            batchMap.computeIfAbsent(batchName, k -> new ArrayList<>()).add(dto);
        }
 
        // 🔥 CALCULATE DIFFERENCE FROM PREVIOUS
        for (List<BatchExamTrendDTO> exams : batchMap.values()) {
 
            Double prev = null;
 
            for (BatchExamTrendDTO e : exams) {
                if (prev == null) {
                    e.setDiffFromPrevious(0.0);
                } else {
                    e.setDiffFromPrevious(e.getPercentage() - prev);
                }
                prev = e.getPercentage();
            }
        }
 
        return batchMap.entrySet().stream()
                .map(e -> BatchAttendanceResponseDTO.builder()
                        .batch(e.getKey())
                        .exams(e.getValue())
                        .build())
                .toList();
    }
 
 
    @Override
    public Map<String, List<StudentRiskDTO>> getStudentRisk(String subdomain, String branch, String batch) {
 
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));
 
    Long tenantId = tenant.getTenantId();
 
    if (!userRepository.existsByTenantIdAndBranchAndBatch(tenantId, branch, batch)) {
        throw new RuntimeException("Invalid branch/batch for tenant");
    }
 
    List<StudentRiskDTO> lowParticipation =
        repository.getLowParticipationStudents(tenantId, branch, batch)
                .stream()
                .map((Object[] r) -> {
 
                    int scheduled = ((Number) r[5]).intValue();
                    int attempted = ((Number) r[6]).intValue();
                    int unattempted = scheduled - attempted;
 
                    double percent = scheduled == 0 ? 0 :
                            (attempted * 100.0) / scheduled;
 
                    return new StudentRiskDTO(
                            ((Number) r[0]).longValue(),
                            (String) r[1],
                            (String) r[2],
                            (String) r[3],
                            (String) r[4],
                            scheduled,
                            attempted,
                            unattempted,
                            percent,
                            null,
                            participationSummary(percent, unattempted)
                    );
                })
                .toList();
 
    List<StudentRiskDTO> lowScore = repository.getLowScoreStudents(tenantId, branch, batch)
    .stream().map((Object[] r) -> {
 
 
            double avg = ((Number) r[5]).doubleValue();
            double percent = (avg / 180.0) * 100;
 
            return StudentRiskDTO.builder()
                    .userId(((Number) r[0]).longValue())
                    .userName((String) r[1])
                    .enrollmentId((String) r[2])
                    .branch((String) r[3])
                    .batch((String) r[4])
                    .avgMarks(avg)
                    .percentage(percent)
                    .summary(scoreSummary(percent))
                    .build();
        }).toList();
 
    return Map.of(
            "lowParticipation", lowParticipation,
            "lowPerformance", lowScore
    );
}
 
private String participationSummary(double percent, int missed) {
 
    if (percent < 50) {
        return "Missed " + missed + " tests • Immediate action needed";
    } else if (percent < 70) {
        return "Missed " + missed + " tests • Needs improvement";
    } else {
        return "Regular participation";
    }
}
 
private String scoreSummary(double percent) {
 
    if (percent < 50) {
        return "Immediate action needed";
    } else if (percent < 70) {
        return "Below average performance";
    } else {
        return "Good performance";
    }
}
private String buildParticipationSummary(double percent, int missed) {
    if (percent < 50) return "Missed " + missed + " tests • Immediate action needed";
    if (percent < 70) return "Attendance dropping • Monitor closely";
    return "Stable participation";
}
 
private String buildScoreSummary(double percent) {
    if (percent < 50) return "Low performance • Immediate action needed";
    if (percent < 70) return "Performance declining • Needs support";
    return "Performance stable";
}
 
 
@Override
public List<BatchAIInsightDTO> getBatchAIInsights(
    String subdomain,
    String branch,
    String batch
) {
 
Tenant tenant = tenantRepository.findBySubdomain(subdomain)
        .orElseThrow(() -> new RuntimeException("Tenant not found"));
 
Long tenantId = tenant.getTenantId();
 
if (!userRepository.existsByTenantIdAndBranchAndBatch(tenantId, branch, batch)) {
    throw new RuntimeException("Invalid branch or batch for this tenant");
}
 
List<BatchAIInsightDTO> result = new ArrayList<>();
 
// LOW PARTICIPATION
for (Object[] r : repository.findLowParticipation(tenantId, branch, batch)) {
 
    int scheduled = ((Number) r[3]).intValue();
    int attempted = ((Number) r[4]).intValue();
    int missed = scheduled - attempted;
 
    result.add(BatchAIInsightDTO.builder()
            .tenantId(tenantId)
            .branch(branch)
            .batch(batch)
            .userId(((Number) r[0]).longValue())
            .name((String) r[1])
            .enrollmentId((String) r[2])
            .reason("Low Participation")
            .details("Skipped " + missed + " out of " + scheduled + " tests")
            .recommendedAction(getRecommendedAction("Low Participation"))
            .build());
}
 
// HIGH NEGATIVE MARKS
for (Object[] r : repository.findHighNegativeMarks(tenantId, branch, batch)) {
 
    result.add(BatchAIInsightDTO.builder()
            .tenantId(tenantId)
            .branch(branch)
            .batch(batch)
            .userId(((Number) r[0]).longValue())
            .name((String) r[1])
            .enrollmentId((String) r[2])
            .reason("High Negative Marks")
            .details("Average " + r[3] + " negative marks per test")
            .recommendedAction(getRecommendedAction("High Negative Marks"))
            .build());
}
 
// FALLING SCORE
for (Object[] r : repository.findFallingScore(tenantId, branch, batch)) {
 
    result.add(BatchAIInsightDTO.builder()
            .tenantId(tenantId)
            .branch(branch)
            .batch(batch)
            .userId(((Number) r[0]).longValue())
            .name((String) r[1])
            .enrollmentId((String) r[2])
            .reason("Falling Score")
            .details("Score dropped more than 20% in last 3 tests")
            .recommendedAction(getRecommendedAction("Falling Score"))
            .build());
}
 
return result;
}
 
 
 
private String getRecommendedAction(String reason) {
 
    switch (reason) {
 
        case "Low Participation":
            return "Regular Attendance Monitoring & Parent Counselling";
 
        case "High Negative Marks":
            return "Accuracy Drill & Concept Clarity Practice Sessions";
 
        case "Falling Score":
            return "Performance Review & Targeted Mentoring Sessions";
 
        default:
            return "Monitor Student Progress";
    }
}
 
}
 
 
 
 