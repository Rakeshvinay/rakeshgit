package com.brihathi.Multi_Tenant.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.brihathi.Multi_Tenant.repository.DifficultyWisePerformanceRepository;
import com.brihathi.Multi_Tenant.repository.SubjectWisePerformanceRepository;
import com.brihathi.Multi_Tenant.repository.TimeAnalysisRepository;
import com.brihathi.Multi_Tenant.repository.ScoreProgressRepository;
import com.brihathi.Multi_Tenant.repository.LeadershipBoardRepository;
import com.brihathi.Multi_Tenant.repository.ScorePredictorRepository;
import com.brihathi.Multi_Tenant.dto.*;
import java.util.List;
import java.time.LocalDateTime;
import com.brihathi.Multi_Tenant.entity.YourScoreProgress;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.YourScoreProgressRepository;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
 
@Service
public class DashboardService {
 
    @Autowired private DifficultyWisePerformanceRepository performanceRepository;
    @Autowired private SubjectWisePerformanceRepository subjectwiseperformanceRepository;
    @Autowired private TimeAnalysisRepository timeAnalysisRepository;
    @Autowired private ScoreProgressRepository scoreProgressRepository;
    @Autowired private LeadershipBoardRepository leaderboardRepository;
    @Autowired private ScorePredictorRepository scorePredictorRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private YourScoreProgressRepository yourScoreProgressRepository;
 
   public List<DifficultyWisePerformanceDTO> getDifficultyWisePerformance(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return performanceRepository.getDifficultyWisePerformance(userId, startDate, endDate);
    } 
    public List<SubjectWisePerformanceDTO> getSubjectWisePerformance(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return subjectwiseperformanceRepository.getSubjectWisePerformance(userId, startDate, endDate);
    }
 
    // public List<TimeAnalysisDTO> getTimeAnalysis(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
    //     return timeAnalysisRepository.getTimeAnalysisByUserAndDateRange(userId, startDate, endDate);
    // }
    public List<Object[]> getTimeAnalysis(Long userId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        return timeAnalysisRepository.getSubjectTimeAnalysisWithRawPercentage(userId, startDate, endDate);
    }
 
    public List<ScoreProgressResponseDTO> getScoreProgress(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Find user's first exam date
        java.sql.Timestamp firstExamTs = yourScoreProgressRepository.findFirstExamDate(userId);
        LocalDateTime firstExamDate = (firstExamTs != null) ? firstExamTs.toLocalDateTime() : startDate;
        // Use firstExamDate as the base for week calculation
        List<Object[]> rows = yourScoreProgressRepository.getScoreProgressByWeek(userId, firstExamDate, endDate);
        Map<String, List<ScoreProgressResponseDTO.WeekStat>> subjectToWeeks = new java.util.LinkedHashMap<>();
        for (Object[] row : rows) {
            String subject = (String) row[0];
            int week = ((Number) row[1]).intValue();
            Double avg = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Double total = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
            Integer count = row[4] != null ? ((Number) row[4]).intValue() : 0;
            subjectToWeeks
                .computeIfAbsent(subject, k -> new java.util.ArrayList<>())
                .add(new ScoreProgressResponseDTO.WeekStat(week, avg, total, count));
        }
        // Set diffFromPreviousWeek for each subject's weeks
        for (List<ScoreProgressResponseDTO.WeekStat> weeks : subjectToWeeks.values()) {
            Double prev = null;
            for (ScoreProgressResponseDTO.WeekStat ws : weeks) {
                if (prev == null) {
                    ws.setDiffFromPreviousWeek(null);
                } else {
                    ws.setDiffFromPreviousWeek(ws.getAveragePercentage() - prev);
                }
                prev = ws.getAveragePercentage();
            }
        }
        // Fetch subject-level summary (still use original startDate)
        List<Object[]> summaryRows = yourScoreProgressRepository.getScoreProgressSubjectSummary(userId, startDate, endDate);
        Map<String, ScoreProgressResponseDTO.Summary> subjectToSummary = new java.util.HashMap<>();
        for (Object[] row : summaryRows) {
            String subject = (String) row[0];
            Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            Double total = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Integer count = row[3] != null ? ((Number) row[3]).intValue() : 0;
            subjectToSummary.put(subject, new ScoreProgressResponseDTO.Summary(avg, total, count));
        }
        List<ScoreProgressResponseDTO> result = subjectToWeeks.entrySet().stream()
            .map(e -> new ScoreProgressResponseDTO(e.getKey(), e.getValue(), subjectToSummary.get(e.getKey())))
            .collect(java.util.stream.Collectors.toList());
        return result;
    }
 
    // public List<LeadershipBoardTopScoreDTO> getTopTotalMarks() {
    //     return leaderboardRepository.getMaxTotalMarksByUser();
    // }
    public List<LeadershipBoardTopScoreDTO> getTopTotalMarks(Long userId) {
        return leaderboardRepository.getTop5AndCurrentUser(userId);
    }
   
    public PredictedRankDTO getPredictedRank(Long userId) {
        return scorePredictorRepository.getPredictedRankForUser(userId);
    }
}
