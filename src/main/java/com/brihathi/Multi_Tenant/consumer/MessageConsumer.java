package com.brihathi.Multi_Tenant.consumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.brihathi.Multi_Tenant.config.RabbitMQConfig;
import com.brihathi.Multi_Tenant.dto.MessageDTO;
import com.brihathi.Multi_Tenant.service.MysteryBoxService;
import com.brihathi.Multi_Tenant.service.MysteryBoxDataHolder;
import com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO;
import java.util.List;
 
 
import org.springframework.beans.factory.annotation.Autowired;
import com.brihathi.Multi_Tenant.service.*;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Component
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
 
    @Autowired
    private ExamResultSummaryService examResultSummaryService;
 
    @Autowired
    private ScoreProgressService scoreProgressService;
 
    @Autowired
    private LeadershipBoardService leadershipBoardService;
 
    @Autowired
    private ScorePredictorService scorePredictorService;
 
    @Autowired
    private TimeAnalysisService timeAnalysisService;
 
    @Autowired
    private SubjectWisePerformanceService subjectWisePerformanceService;
    // @Autowired
    // private ErrorTrackerDataHolder dataHolder;
 
    @Autowired
    private ErrorTrackerService errorTrackerService;

    @Autowired
    private MysteryBoxService mysteryBoxService;
    @Autowired
    private MysteryBoxDataHolder mysteryBoxDataHolder;

 
     
    @RabbitListener(queues = RabbitMQConfig.EXAM_RESULTS_QUEUE)
    public void handleExamResult(MessageDTO dto) {
        logger.info("Rabbit received on EXAM_RESULTS_QUEUE: {}", dto);
        if ("EXAM_RESULT".equals(dto.getType())) {
            logger.info("Dispatching createExamResultSummary: examId={}, userId={}", dto.getExamId(), dto.getUserId());
            examResultSummaryService.createExamResultSummary(dto.getExamId(), dto.getUserId());
        } else {
            logger.info("Ignoring message on EXAM_RESULTS_QUEUE due to type mismatch: type={}", dto.getType());
        }
    }
 
    @RabbitListener(queues = RabbitMQConfig.CHAPTER_RESULTS_QUEUE)
    public void handleChapterResults(MessageDTO dto) {
        logger.info("Rabbit received on CHAPTER_RESULTS_QUEUE: {}", dto);
        if ("CHAPTER_RESULTS".equals(dto.getType())) {
            logger.info("Dispatching createChapterWiseResults: examId={}, userId={}", dto.getExamId(), dto.getUserId());
            examResultSummaryService.createChapterWiseResults(dto.getExamId(), dto.getUserId());
        } else {
            logger.info("Ignoring message on CHAPTER_RESULTS_QUEUE due to type mismatch: type={}", dto.getType());
        }
    }
 
    @RabbitListener(queues = RabbitMQConfig.SCORE_PREDICTOR_QUEUE)
    public void handleScorePredictor(MessageDTO dto) {
        if ("SCORE_PREDICTOR".equals(dto.getType())) {
            scorePredictorService.aggregateAndUpsertScorePredictor(dto.getUserId());
        }
    }
 
    @RabbitListener(queues = RabbitMQConfig.TIME_ANALYSIS_QUEUE)
    public void handleTimeAnalysis(MessageDTO dto) {
        if ("TIME_ANALYSIS".equals(dto.getType())) {
            timeAnalysisService.aggregateAndInsertTimeAnalysis(dto.getExamId(), dto.getUserId());
        }
    }
 
    @RabbitListener(queues = RabbitMQConfig.SUBJECT_WISE_PERFORMANCE_QUEUE)
    public void handleSubjectWisePerformance(MessageDTO dto) {
        if ("SUBJECT_WISE_PERFORMANCE".equals(dto.getType())) {
            subjectWisePerformanceService.aggregateAndInsertSubjectWisePerformance(dto.getExamId(), dto.getUserId());
        }
    }
 
 
    @RabbitListener(queues = RabbitMQConfig.SCORE_PROGRESS_QUEUE)
    public void handleScoreProgress(MessageDTO dto) {
        if ("SCORE_PROGRESS".equals(dto.getType())) {
            scoreProgressService.createScoreProgress(dto.getExamId(), dto.getUserId());
        }
    }
 
    @RabbitListener(queues = RabbitMQConfig.LEADERSHIP_BOARD_QUEUE)
    public void handleLeadershipBoard(MessageDTO dto) {
        if ("LEADERSHIP_BOARD".equals(dto.getType())) {
            leadershipBoardService.createLeadershipBoardEntry(dto.getExamId(), dto.getUserId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.ERROR_TRACKER_QUEUE)
    public void handleErrorTracker(MessageDTO dto) {
        if ("ERROR_TRACKER".equals(dto.getType())) {
            // TODO: Implement error tracking logic here
            errorTrackerService.syncErrors(dto.getUserId(), dto.getExamId());
            System.out.println("RAW ERROR_TRACKER message: ");
       
        }

}

 
  
@RabbitListener(queues = RabbitMQConfig.MYSTERY_BOX_QUEUE)
public void handleMysteryBox(MessageDTO dto) {
    if ("MYSTERY_BOX".equals(dto.getType())) {
        mysteryBoxService.processMysteryBox(dto.getUserId(), dto.getExamId());
 
        // --- MysteryBox counts ---
    List<SubjectQuestionCountDTO> mbDeleteCounts =
           mysteryBoxService.getDeleteCounts(dto.getUserId(), dto.getExamId());
    List<SubjectQuestionCountDTO> mbInsertCounts =
            mysteryBoxService.getInsertCounts(dto.getUserId(), dto.getExamId());
            mysteryBoxDataHolder.updateCounts(dto.getUserId(), mbDeleteCounts, mbInsertCounts);
 
    logger.info("✅ Updated counts in memory for userId={}.  MB Delete={} Insert={}",
            dto.getUserId(), mbDeleteCounts.size(), mbInsertCounts.size());
    }
}
 
}
 
 