package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.ScheduledScorePredictorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class ScheduledScorePredictorServiceImpl
        implements ScheduledScorePredictorService {

    @Autowired
    private ScheduledChapterWiseResultRepository chapterWiseResultRepository;

    @Autowired
    private ScheduledScorePredictorRepository scorePredictorRepository;

    @Autowired
    private ScheduledSubjectWisePerformanceBarRepository subjectWisePerformanceRepository;

    /* =====================================================
       SCORE PREDICTOR (EXTERNAL ML API)
       ===================================================== */

//        @Override
//        @Transactional
//        public void aggregateAndUpsertScorePredictor(Long userId) {
       
//            List<ScheduledChapterWiseResult> allResults =
//                    chapterWiseResultRepository.findByUserId(userId);
       
//            if (allResults == null || allResults.isEmpty()) return;
       
//            // 🔥 Take any one exam row (latest is better)
//            ScheduledChapterWiseResult latestExam = allResults.get(0);
       
//            double avgScore = allResults.stream()
//                    .mapToInt(ScheduledChapterWiseResult::getMarks)
//                    .average()
//                    .orElse(0.0);
       
//            Integer predictedRank = callExternalRankPredictionAPI(avgScore);
       
//            ScheduledScorePredictor predictor =
//                    scorePredictorRepository.findByUserId(userId)
//                            .orElse(
//                                    ScheduledScorePredictor.builder()
//                                            .uuid(UUID.randomUUID())
//                                            .userId(userId)
//                                            .build()
//                            );
       
//            // ✅ FILL MISSING COLUMNS
//            predictor.setTenantId(latestExam.getTenantId());
//            predictor.setBranchId(latestExam.getBranchId());
//            predictor.setBatchId(latestExam.getBatchId());
       
//            predictor.setPredictedScore(avgScore);
//            predictor.setPredictedRank(predictedRank);
       
//            long noOfExams = allResults.stream()
//                    .map(ScheduledChapterWiseResult::getEduScheduledExamId)
//                    .distinct()
//                    .count();
       
//            predictor.setNoOfExams((int) noOfExams);
       
//            // percentage = overall average %
//            predictor.setPercentage(avgScore);
       
//            List<ScheduledSubjectWisePerformanceBar> subjectBars =
//                    subjectWisePerformanceRepository.findByUserId(userId);
       
//            Map<String, Double> subjectAverages =
//                    subjectBars.stream()
//                            .collect(Collectors.groupingBy(
//                                    ScheduledSubjectWisePerformanceBar::getSubject,
//                                    Collectors.averagingDouble(
//                                            s -> s.getPercentage() != null
//                                                    ? s.getPercentage().doubleValue()
//                                                    : 0.0
//                                    )
//                            ));
       
//            predictor.setGoodAt(
//                    subjectAverages.entrySet().stream()
//                            .filter(e -> e.getValue() >= 60)
//                            .map(Map.Entry::getKey)
//                            .collect(Collectors.joining(", "))
//            );
       
//            predictor.setNeedToImprove(
//                    subjectAverages.entrySet().stream()
//                            .filter(e -> e.getValue() < 60)
//                            .map(Map.Entry::getKey)
//                            .collect(Collectors.joining(", "))
//            );
       
//            scorePredictorRepository.save(predictor);
//        }
@Override
@Transactional
public void aggregateAndUpsertScorePredictor(Long userId) {

    List<ScheduledChapterWiseResult> allResults =
            chapterWiseResultRepository.findByUserId(userId);

    if (allResults.isEmpty()) return;

    ScheduledChapterWiseResult latestExam = allResults.get(0);

    double avgScore = allResults.stream()
            .mapToInt(ScheduledChapterWiseResult::getMarks)
            .average()
            .orElse(0.0);

    Integer predictedRank = callExternalRankPredictionAPI(avgScore);

    try {

        ScheduledScorePredictor predictor =
                scorePredictorRepository.findByUserIdForUpdate(userId) // 🔒 lock row
                        .orElse(
                                ScheduledScorePredictor.builder()
                                        .uuid(UUID.randomUUID())
                                        .userId(userId)
                                        .build()
                        );

        predictor.setTenantId(latestExam.getTenantId());
        predictor.setBranchId(latestExam.getBranchId());
        predictor.setBatchId(latestExam.getBatchId());
        predictor.setPredictedScore(avgScore);
        predictor.setPredictedRank(predictedRank);
        predictor.setPercentage(avgScore);

        long noOfExams = allResults.stream()
                .map(ScheduledChapterWiseResult::getEduScheduledExamId)
                .distinct()
                .count();

        predictor.setNoOfExams((int) noOfExams);

        scorePredictorRepository.save(predictor);

    } catch (Exception e) {
        // If another thread inserted → retry update
        ScheduledScorePredictor existing =
                scorePredictorRepository.findByUserId(userId).get();

        existing.setPredictedScore(avgScore);
        existing.setPredictedRank(predictedRank);
        existing.setPercentage(avgScore);

        scorePredictorRepository.save(existing);
    }
}


       private Integer callExternalRankPredictionAPI(double avgScore) {

        try {
            RestTemplate restTemplate = new RestTemplate();
    
            String url = "http://37.27.191.235:5000/predict_rank";
    
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", "QPy1Swwhow2kedhBrIPvpTa4IgnX154T");
    
            Map<String, Object> body = new HashMap<>();
            body.put("score", Math.round(avgScore));
    
            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);
    
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);
    
            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().get("predicted_rank") != null) {
    
                return Integer.parseInt(
                        response.getBody().get("predicted_rank").toString()
                );
            }
    
        } catch (Exception e) {
            System.out.println("Rank API failed, using fallback");
        }
    
        return 500_000; // fallback rank
    }
    
       
}
