// package com.brihathi.Multi_Tenant.service;
 
// import org.springframework.stereotype.Service;
// import org.springframework.beans.factory.annotation.Autowired;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
// import java.util.UUID;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import java.util.HashMap;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
 
// import com.brihathi.Multi_Tenant.repository.ChapterResultRepository;
// import com.brihathi.Multi_Tenant.entity.ChapterResult;
// import com.brihathi.Multi_Tenant.enums.Subject;
// import com.brihathi.Multi_Tenant.repository.ScheduledScorePredictorRepository;
// import com.brihathi.Multi_Tenant.entity.ScheduledScorePredictor;
// import com.brihathi.Multi_Tenant.repository.UserRepository;
// import com.brihathi.Multi_Tenant.entity.User;
// import com.brihathi.Multi_Tenant.repository.ScheduledSubjectWisePerformanceBarRepository;
// import com.brihathi.Multi_Tenant.entity.ScheduledSubjectWisePerformanceBar;
// import com.brihathi.Multi_Tenant.repository.ScheduledAverageForPredictedScoreRepository;
 
// @Service
// public class ScheduledScorePredictorService {
//     private static final Logger logger = LoggerFactory.getLogger(ScheduledScorePredictorService.class);
 
//     @Autowired
//     private ChapterResultRepository chapterResultRepository;
 
//     @Autowired
//     private ScheduledScorePredictorRepository scorePredictorRepository;
 
//     @Autowired
//     private UserRepository userRepository;
 
//     @Autowired
//     private ScheduledSubjectWisePerformanceBarRepository subjectWisePerformanceBarRepository;
 
//     private final ScheduledAverageForPredictedScoreRepository averageForPredictedScoreRepository;
 
//     // Inject via constructor (add to existing constructor if present)
//     public ScheduledScorePredictorService(ScheduledAverageForPredictedScoreRepository averageForPredictedScoreRepository /*, other dependencies */) {
//         this.averageForPredictedScoreRepository = averageForPredictedScoreRepository;
//         // ... initialize other dependencies ...
//     }
 
//     public void aggregateAndUpsertScorePredictor(Long userId) {
//         // 1. Fetch all chapter_results for this user and exam type 'ALL'
//      //   List<ChapterResult> allExams = chapterResultRepository.findByUserIdAndExamType(userId, Subject.ALL);
//         List<ChapterResult> allExams = chapterResultRepository.findByUserId(userId);
//         if (allExams.isEmpty()) return;
 
//         // 2. Calculate average score using repository
//         Double avgScore = averageForPredictedScoreRepository.findAvgScoreByUserId(userId);
//         if (avgScore == null) avgScore = 0.0;
 
//         // 4. Predict rank using external API
//         Integer predictedRank = null;
//         try {
//             RestTemplate restTemplate = new RestTemplate();
//             String url = "http://37.27.191.235:5000/predict_rank";
//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_JSON);
//             headers.set("x-api-key", "QPy1Swwhow2kedhBrIPvpTa4IgnX154T");
//             HashMap<String, Object> body = new HashMap<>();
//             body.put("score", Math.round(avgScore));
//             HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(body, headers);
//             ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
//             if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().get("predicted_rank") != null) {
//                 predictedRank = Integer.parseInt(response.getBody().get("predicted_rank").toString());
//             }
//             //  else {
//             //     // fallback or log error
//             //     predictedRank = 500000;
//             // }
//         } catch (Exception e) {
//             // fallback or log error
//             predictedRank = 500000;
//         }
 
//         // 5. Upsert into Score_Predictor_Table
//         ScheduledScorePredictor predictor = scorePredictorRepository.findByUserId(userId).orElse(new ScheduledScorePredictor());
 
//         User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//         predictor.setUserId(userId);
//         predictor.setPredictedScore(avgScore);
//         predictor.setPredictedRank(predictedRank);
       
//         // Get exact count of unique exams from repository
//         List<Long> distinctExamIds = chapterResultRepository.findDistinctExamIdsByUserId(userId);
//         int noOfExams = distinctExamIds.size();
//         logger.info("User {} has {} distinct exams. ExamIds: {}", userId, noOfExams, distinctExamIds);
//         predictor.setNoOfExams(noOfExams);
 
//         if (predictor.getUuid() == null) {
//             predictor.setUuid(UUID.randomUUID());
//         }
 
//         // --- Subject-wise analysis for goodAt and needToImprove ---
//         List<ScheduledSubjectWisePerformanceBar> subjectPerformances = subjectWisePerformanceBarRepository.findByUserId(userId);
//         Map<String, Double> subjectAverages = subjectPerformances.stream()
//             .collect(Collectors.groupingBy(
//                 ScheduledSubjectWisePerformanceBar::getSubject,
//                 Collectors.averagingDouble(s -> s.getPercentage() != null ? s.getPercentage().doubleValue() : 0.0)
//             ));
//         List<String> goodAtSubjects = subjectAverages.entrySet().stream()
//             .filter(e -> e.getValue() >= 60.0)
//             .map(Map.Entry::getKey)
//             .collect(Collectors.toList());
//         List<String> needToImproveSubjects = subjectAverages.entrySet().stream()
//             .filter(e -> e.getValue() < 60.0)
//             .map(Map.Entry::getKey)
//             .collect(Collectors.toList());
//         predictor.setGoodAt(String.join(", ", goodAtSubjects));
//         predictor.setNeedToImprove(String.join(", ", needToImproveSubjects));
//         // System.out.println("Predicted Rank before save: " + predictor.getPredictedRank());
//         scorePredictorRepository.save(predictor);
//     }
// }
package com.brihathi.Multi_Tenant.service;

public interface ScheduledScorePredictorService {

    void aggregateAndUpsertScorePredictor(Long userId);
}
