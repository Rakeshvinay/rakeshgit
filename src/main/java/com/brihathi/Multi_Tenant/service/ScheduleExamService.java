package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.dto.CreateScheduledExamRequest;
import com.brihathi.Multi_Tenant.dto.ScheduleExamResponse;
import com.brihathi.Multi_Tenant.dto.AllScheduleExamsResDTO;
import com.brihathi.Multi_Tenant.dto.ScheduledExamViewDTO;
import com.brihathi.Multi_Tenant.dto.ExamOverviewDTO;
import com.brihathi.Multi_Tenant.dto.EducatorExamListDTO;
import com.brihathi.Multi_Tenant.dto.StartScheduledExamResponse;
import com.brihathi.Multi_Tenant.dto.UpdateScheduledExamRequest;
import com.brihathi.Multi_Tenant.entity.ScheduledExam;
import com.brihathi.Multi_Tenant.entity.EducatorScheduledExam;
import java.util.Map;

import java.util.List;
import java.util.Optional;

public interface ScheduleExamService {

    ScheduleExamResponse createScheduledExam(CreateScheduledExamRequest request,String subdomain);
    StartScheduledExamResponse startScheduledExam(Long userId,Long scheduledExamId);
    Map<String, Object> getGeneratedQids(Long userId, Long scheduledExamId);
    Map<String, Object> updateQuestion( Long userId, Long scheduledExamId, Map<String, Object> questionUpdate);
    Map<String, Object> endScheduledExam(Long userId, Long scheduledExamId);
    Map<String, Object> getQuestionByQid(String qid,String subdomain);
    ScheduledExam abortScheduledExam(Long userId, Long scheduledExamId);
    Map<String, Object> confirmSubmission(Long userId, Long scheduledExamId);
    Map<String, Object> getFinalResults(Long userId, Long scheduledExamId);
    List<AllScheduleExamsResDTO> getAllScheduledExams(Long userId);

//     List<EducatorScheduledExam> findScheduledExamsByFilters(
//         String subdomain,
//         String branch,
//         String batch,
//         String examStatus
// );
List<EducatorExamListDTO> findScheduledExamsByFilters(
        String subdomain,
        String branch,
        String batch,
        String examStatus
);

Optional<ScheduledExam> getScheduledExamById(Long scheduledExamId);

void cancelScheduledExam(Long eduScheduledExamId, String subdomain);
void updateScheduledExam(Long eduScheduledExamId,
    UpdateScheduledExamRequest request,
    String subdomain);


    ExamOverviewDTO getExamOverview(String subdomain);

}
