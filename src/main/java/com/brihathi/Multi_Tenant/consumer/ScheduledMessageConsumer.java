// package com.brihathi.Multi_Tenant.consumer;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;
// import com.brihathi.Multi_Tenant.config.RabbitMQConfig;
// import com.brihathi.Multi_Tenant.dto.MessageDTO;
// import com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO;

// import org.springframework.beans.factory.annotation.Autowired;
// import com.brihathi.Multi_Tenant.service.ScheduledExamResultSummaryService;
// import com.brihathi.Multi_Tenant.service.ScheduledScorePredictorService;
// import com.brihathi.Multi_Tenant.service.ScheduledScoreProgressService;
// import com.brihathi.Multi_Tenant.service.ScheduledSubjectWisePerformanceService;
// import com.brihathi.Multi_Tenant.service.ScheduledDifficultyWisePerformanceService;
// import com.brihathi.Multi_Tenant.service.ScheduledLeadershipBoardService;
// import com.brihathi.Multi_Tenant.service.ScheduledTimeAnalysisService;
// import com.brihathi.Multi_Tenant.serviceimpl.ScheduledLeadershipBoardServiceImpl;

// import java.util.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
 
// @Component
// public class MessageConsumer {
//     private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
 
//     @Autowired
//     private ScheduledExamResultSummaryService scheduledExamResultSummaryService;
 
//     @Autowired
//     private ScheduledScoreProgressService scheduledScoreProgressService;
 
//     @Autowired
//     private ScheduledLeadershipBoardService scheduledLeadershipBoardService;
 
//     @Autowired
//     private ScheduledScorePredictorService scheduledScorePredictorService;
 
//     @Autowired
//     private ScheduledTimeAnalysisService timeAnalysisService;
 
//     @Autowired
//     private ScheduledSubjectWisePerformanceService scheduledSubjectWisePerformanceService;
 
//     // @Autowired
//     // private DifficultyWisePerformanceService difficultyWisePerformanceService;

//     // @Autowired
//     // private ErrorTrackerDataHolder dataHolder;

//     // @Autowired
//     // private ErrorTrackerService errorTrackerService;

     
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_EXAM_RESULTS_QUEUE)
//     public void handleExamResult(MessageDTO dto) {
//         if ("SCHEDULED_EXAM_RESULT".equals(dto.getType())) {
//             scheduledExamResultSummaryService.createExamResultSummary(dto.getEduScheduledExamId(), dto.getUserId());
//         }
//     }
 
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_CHAPTER_RESULTS_QUEUE)
//     public void handleChapterResults(MessageDTO dto) {
//         System.out.println("received msg from SCHEDULED_CHAPTER_RESULTS_QUEUE");
//         if ("SCHEDULED_CHAPTER_RESULTS".equals(dto.getType())) {
//             scheduledExamResultSummaryService.createChapterWiseResults(dto.getEduScheduledExamId(), dto.getUserId());
//         }
//     }
 
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_SCORE_PREDICTOR_QUEUE)
//     public void handleScorePredictor(MessageDTO dto) {
//         if ("SCHEDULED_SCORE_PREDICTOR".equals(dto.getType())) {
//             scheduledScorePredictorService.aggregateAndUpsertScorePredictor(dto.getUserId());
//         }
//     }
 
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_TIME_ANALYSIS_QUEUE)
//     public void handleTimeAnalysis(MessageDTO dto) {
//         if ("SCHEDULED_TIME_ANALYSIS".equals(dto.getType())) {
//             timeAnalysisService.aggregateAndInsertTimeAnalysis(dto.getEduScheduledExamId(), dto.getUserId());
//         }
//     }
 
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE)
//     public void handleSubjectWisePerformance(MessageDTO dto) {
//         if ("SCHEDULED_SUBJECT_WISE_PERFORMANCE".equals(dto.getType())) {
//             scheduledSubjectWisePerformanceService.aggregateAndInsertSubjectWisePerformance(dto.getEduScheduledExamId(), dto.getUserId());
//         }
//     }
 
 
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_SCORE_PROGRESS_QUEUE)
//     public void handleScoreProgress(MessageDTO dto) {
//         if ("SCHEDULED_SCORE_PROGRESS".equals(dto.getType())) {
//             scheduledScoreProgressService.createScoreProgress(dto.getEduScheduledExamId(), dto.getUserId());
//         }
//     }
 
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_LEADERSHIP_BOARD_QUEUE)
//     public void handleLeadershipBoard(MessageDTO dto) {
//         if ("SCHEDULED_LEADERSHIP_BOARD".equals(dto.getType())) {
//             scheduledLeadershipBoardService.createLeadershipBoardEntry(dto.getEduScheduledExamId(), dto.getUserId());
//         }
//     }
// //     @RabbitListener(queues = RabbitMQConfig.ERROR_TRACKER_MAINS_QUEUE)
// //     public void handleErrorTracker(MessageDTO dto) {
// //         if ("ERROR_TRACKER".equals(dto.getType())) {
// //             // TODO: Implement error tracking logic here
// //             errorTrackerService.syncErrors(dto.getUserId(), dto.getExamId());
// //             System.out.println("RAW ERROR_TRACKER message: ");
       
// //         }
// //         //System.out.println("RAW ERROR_TRACKER message: ");
// //     }
// //     @RabbitListener(queues = RabbitMQConfig.ERROR_TRACKING_TRENDS_MAINS_QUEUE)
// // public void handleErrorTrackerSync(MessageDTO dto) {
// //     if ("ERROR_TRACKING_TRENDS".equals(dto.getType())) {
// //         // Fetch counts for this specific user and exam
// //         List<SubjectQuestionCountDTO> deleteCounts =
// //                 errorTrackerService.getDeleteCounts(dto.getUserId(), dto.getExamId());

// //         List<SubjectQuestionCountDTO> insertCounts =
// //                 errorTrackerService.getInsertCounts(dto.getUserId(), dto.getExamId());

// //         // Store counts for this user in memory
// //         dataHolder.updateCounts(dto.getUserId(), deleteCounts, insertCounts);

// //         logger.info("✅ Updated counts in memory for userId={}.ET DeleteCountSize={} InsertCountSize={}",
// //                 dto.getUserId(), deleteCounts.size(), insertCounts.size());
// //     }
// // }
// }

package com.brihathi.Multi_Tenant.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.brihathi.Multi_Tenant.config.ScheduledRabbitMQConfig;
import com.brihathi.Multi_Tenant.dto.ScheduledMessageDTO;
import com.brihathi.Multi_Tenant.service.*;
import com.brihathi.Multi_Tenant.context.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.entity.*;
@Component
public class ScheduledMessageConsumer {
    @Autowired
    private TenantContext tenantContext;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired private ScheduledExamResultSummaryService examResultService;
    @Autowired private ScheduledScorePredictorService scorePredictorService;
    @Autowired private ScheduledScoreProgressService scoreProgressService;
    @Autowired private ScheduledLeadershipBoardService leadershipBoardService;
    @Autowired private ScheduledTimeAnalysisService timeAnalysisService;
    @Autowired private ScheduledSubjectWisePerformanceService subjectWisePerformanceService;
    @Autowired private ScheduledExamRepository scheduledExamRepository;
    @Autowired
private ScheduledDifficultyWisePerformanceService scheduledDifficultyWisePerformanceService;

    /* ================= EXAM RESULT ================= */

    @RabbitListener(queues = ScheduledRabbitMQConfig.SCHEDULED_EXAM_RESULTS_QUEUE)
    public void handleExamResult(ScheduledMessageDTO dto) {
        processWithTenant(dto, () ->
                examResultService.createExamResultSummary(dto.getEduScheduledExamId(), dto.getUserId())

                
        );
        // 2️⃣ NOW fetch student exam
        ScheduledExam exam = scheduledExamRepository
                .findByEduScheduledExamIdAndUserId(
                        dto.getEduScheduledExamId(),
                        dto.getUserId()
                )
                .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

        // 3️⃣ Pass STUDENT scheduledExamId
        scheduledDifficultyWisePerformanceService
                .aggregateAndInsertDifficultyWisePerformance(
                        dto.getUserId(),
                        exam.getScheduledExamId()
                );
    }

    /* ================= CHAPTER RESULT ================= */

    @RabbitListener(queues = ScheduledRabbitMQConfig.SCHEDULED_CHAPTER_RESULTS_QUEUE)
    public void handleChapterResults(ScheduledMessageDTO dto) {
        processWithTenant(dto, () ->
                examResultService.createChapterWiseResults(dto.getEduScheduledExamId(), dto.getUserId())
        );


         // 🔥 TRIGGER SCORE PREDICTOR HERE (guaranteed order)
        //  scorePredictorService.aggregateAndUpsertScorePredictor(dto.getUserId());
    }

    /* ================= SCORE PREDICTOR ================= */

    @RabbitListener(queues = ScheduledRabbitMQConfig.SCHEDULED_SCORE_PREDICTOR_QUEUE)
    public void handleScorePredictor(ScheduledMessageDTO dto) {
        processWithTenant(dto, () ->
                scorePredictorService.aggregateAndUpsertScorePredictor(dto.getUserId())
        );
    }

    /* ================= TIME ANALYSIS ================= */

    @RabbitListener(queues = ScheduledRabbitMQConfig.SCHEDULED_TIME_ANALYSIS_QUEUE)
    public void handleTimeAnalysis(ScheduledMessageDTO dto) {
        processWithTenant(dto, () ->
                timeAnalysisService.aggregateAndInsertTimeAnalysis(dto.getEduScheduledExamId(), dto.getUserId())
        );
    }

    /* ================= SUBJECT PERFORMANCE ================= */

    @RabbitListener(queues = ScheduledRabbitMQConfig.SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE)
    public void handleSubjectWisePerformance(ScheduledMessageDTO dto) {
        processWithTenant(dto, () ->
                subjectWisePerformanceService.aggregateAndInsertSubjectWisePerformance(dto.getEduScheduledExamId(), dto.getUserId())
        );
    }

    /* ================= SCORE PROGRESS ================= */

    @RabbitListener(queues = ScheduledRabbitMQConfig.SCHEDULED_SCORE_PROGRESS_QUEUE)
    public void handleScoreProgress(ScheduledMessageDTO dto) {
        processWithTenant(dto, () ->
                scoreProgressService.createScoreProgress(dto.getEduScheduledExamId(), dto.getUserId())
        );
    }

    /* ================= LEADERBOARD ================= */

    @RabbitListener(queues = ScheduledRabbitMQConfig.SCHEDULED_LEADERSHIP_BOARD_QUEUE)
    public void handleLeadershipBoard(ScheduledMessageDTO dto) {
        processWithTenant(dto, () ->
                leadershipBoardService.createLeadershipBoardEntry(dto.getEduScheduledExamId(), dto.getUserId())
        );
    }

    /* ================= COMMON TENANT HANDLER ================= */

    private void processWithTenant(ScheduledMessageDTO dto, Runnable task) {
        try {
            // Convert tenantId → subdomain (because context stores STRING)
            String tenantSubdomain = tenantRepository.findById(dto.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"))
                    .getSubdomain();
    
            tenantContext.setTenant(tenantSubdomain);  // ✅ CORRECT
    
            System.out.println("📥 CONSUMED → " + dto.getType() +
                    " | user=" + dto.getUserId() +
                    " | exam=" + dto.getEduScheduledExamId() +
                    " | tenant=" + tenantSubdomain);
    
            task.run();
    
            System.out.println("✅ DB INSERT DONE for " + dto.getType());
    
        } catch (Exception e) {
            System.out.println("❌ ERROR in " + dto.getType() + " : " + e.getMessage());
            e.printStackTrace();
        } finally {
            tenantContext.clear();  // ✅ CORRECT
        }
    }
    
//     @RabbitListener(queues = RabbitMQConfig.SCHEDULED_EXAM_RESULTS_QUEUE)
// public void handleExamResult(MessageDTO message) {

//     System.out.println("📥 CONSUMED -> " + message);

//     try {
//         // 🔥 LOAD TENANT NAME FROM ID
//         String tenantSubdomain = tenantRepository
//                 .findById(message.getTenantId())
//                 .orElseThrow(() -> new RuntimeException("Tenant not found"))
//                 .getSubdomain();

//         // 🔥 SET TENANT CONTEXT (same as filter does)
//         tenantContext.setTenant(tenantSubdomain);

//         examResultService.createExamResultSummary(
//                 message.getEduScheduledExamId(),
//                 message.getUserId()
//         );

//     } finally {
//         tenantContext.clear(); // VERY IMPORTANT
//     }
// }

}



