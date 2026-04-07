
package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.dto.*;
import com.brihathi.Multi_Tenant.dto.SubjectWisePerformanceChapterNormalDTO.ChapterDTO;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.repository.ExamRepository;
import com.brihathi.Multi_Tenant.repository.OverallSwotRepository;
import com.brihathi.Multi_Tenant.service.OverallSwotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OverallSwotServiceImpl implements OverallSwotService {

    private final OverallSwotRepository overallSwotRepository;
    private final ExamRepository examRepository;

    @Override
    public SwotNormalResponseDTO generateOverallSwot(Long userId) {
        // Fetch chapter-wise performance
        List<Object[]> rawResults = overallSwotRepository.aggregateChapterPerformanceForAllExams(userId);

        List<SubjectWisePerformanceChapterNormalDTO> chapters = rawResults.stream().map(obj -> {
            SubjectWisePerformanceChapterNormalDTO dto = new SubjectWisePerformanceChapterNormalDTO();
            ChapterDTO chapter = new ChapterDTO();
            chapter.setSubject((String) obj[0]);
            chapter.setName((String) obj[1]);
            dto.setChapter(chapter);
            dto.setMarks(((Number) obj[2]).doubleValue());
            double totalMarks = ((Number) obj[3]).doubleValue();
            dto.setTotalMarks(totalMarks);
            dto.setPercentage(totalMarks == 0 ? 0.0 : (dto.getMarks() * 100.0) / totalMarks);
            dto.setTimeSpent((String) obj[4]);
            return dto;
        }).toList();

        // Group by subject
        Map<String, List<SubjectWisePerformanceChapterNormalDTO>> groupedBySubject = chapters.stream()
                .collect(Collectors.groupingBy(dto -> dto.getChapter().getSubject()));

        Map<String, SwotNormalResponseDTO.SubjectSwot> swotMap = new LinkedHashMap<>();

        for (Map.Entry<String, List<SubjectWisePerformanceChapterNormalDTO>> entry : groupedBySubject.entrySet()) {
            String subject = entry.getKey();
            List<SubjectWisePerformanceChapterNormalDTO> subjectChapters = entry.getValue();

            List<SubjectWisePerformanceChapterNormalDTO> sortedByPercentage = subjectChapters.stream()
                    .sorted(Comparator.comparingDouble(SubjectWisePerformanceChapterNormalDTO::getPercentage))
                    .toList();

            List<String> strengths = subjectChapters.stream()
                    .filter(dto -> dto.getPercentage() >= 70)
                    .sorted(Comparator.comparingDouble(SubjectWisePerformanceChapterNormalDTO::getPercentage).reversed())
                    .limit(2)
                    .map(dto -> dto.getChapter().getName())
                    .toList();

            List<String> lowest = sortedByPercentage.stream()
                    .limit(3)
                    .map(dto -> dto.getChapter().getName())
                    .toList();

            SwotNormalResponseDTO.SubjectSwot subjectSwot = new SwotNormalResponseDTO.SubjectSwot();
            subjectSwot.setSubject(subject);

            if (!strengths.isEmpty()) {
                SwotNormalResponseDTO.Strength strengthsObj = new SwotNormalResponseDTO.Strength();
                strengthsObj.setSubject(subject);
                strengthsObj.setTopChapters(strengths);
                strengthsObj.setMessages(List.of("Nice work! Despite the overall score, you've done well in these chapters:"));
                subjectSwot.setStrengths(strengthsObj);
            }

            SwotNormalResponseDTO.Weakness weakness = new SwotNormalResponseDTO.Weakness();
            weakness.setSubject(subject);
            weakness.setLowestChapters(lowest);
            weakness.setMessages(List.of(
                    "📌 Needs more focused attention in these chapters:",
                    "📌 Now's the time to rebuild confidence:\n📌 Prioritize targeted revision and take chapter-level practice tests to strengthen your fundamentals."
            ));
            subjectSwot.setWeaknesses(weakness);

            SwotNormalResponseDTO.Opportunity opportunity = new SwotNormalResponseDTO.Opportunity();
            opportunity.setSubject(subject);
            opportunity.setLowestChapters(lowest.subList(0, Math.min(2, lowest.size())));
            opportunity.setMessages(List.of(
                    "📌 Stay Focused. Stay Ready. NEET SWAN isn't just a platform—it's your launchpad to success.",
                    "✅ Take frequent mock tests\n✅ Review what clicks and what needs attention\n✅ Practice not until you get it right, but until you can't get it wrong.",
                    "📌 Keep pushing—you're stronger than you think. Do focus on below.",
                    "🟡 Basic difficulty selected. Keep going! Practice more Intermediate and Advanced difficulty-level mock tests to strengthen your preparation."
            ));
            subjectSwot.setOpportunities(opportunity);

            SwotNormalResponseDTO.Threat threat = new SwotNormalResponseDTO.Threat();
            threat.setSubject(subject);
            List<String> threatChapters = lowest;
            List<String> threatMessages = new ArrayList<>();
            threatMessages.add("🛑 Top Chapters with Highest Negative Marks:");
            threatMessages.addAll(threatChapters);
            threatMessages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
            threatMessages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
            threatMessages.addAll(threatChapters);
            threatMessages.add("Attempt chapter-wise specific practice tests – build confidence one step at a time.");
            threat.setMessages(threatMessages);
            subjectSwot.setThreats(threat);

            swotMap.put(subject, subjectSwot);
        }
        
        List<Object[]> aiRawResults = overallSwotRepository.aggregateSubjectInsights(userId);
Map<String, SwotNormalResponseDTO.AiInsightDTO> aiInsightsMap = new HashMap<>();
 
// ? 1. Fetch average marks out of 180 for each subject
Map<String, Double> subjectToAvgMarksOutOf180 = new HashMap<>();
for (Object[] row : overallSwotRepository.findAverageMarks(userId)) {
    if (row[0] != null && row[1] != null) {
        subjectToAvgMarksOutOf180.put(
            (String) row[0],
            Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0
        );
    }
}
 
// ? 2. Fetch average percentage for each subject
Map<String, Double> subjectToAvgPercentage = new HashMap<>();
for (Object[] row : overallSwotRepository.findAvgPercentage(userId)) {
    if (row[0] != null && row[1] != null) {
        subjectToAvgPercentage.put(
            (String) row[0],
            Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0
        );
    }
}
 
// ? 3. Fetch average time spent (minutes) for each subject
Map<String, Double> subjectToAvgTimeSpent = new HashMap<>();
for (Object[] row : overallSwotRepository.findAverageTimeSpentPerSubject(userId)) {
    if (row[0] != null && row[1] != null) {
        subjectToAvgTimeSpent.put(
            (String) row[0],
            Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0
        );
    }
}
 
// ? 4. Fetch negative marks for each subject
Map<String, Double> subjectToAvgNegativeMarks = new HashMap<>();
for (Object[] row : overallSwotRepository.getNegativeMarksSummary(userId)) {
    if (row[0] != null && row[1] != null) {
        subjectToAvgNegativeMarks.put(
            (String) row[0],
            Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0
        );
    }
}
 
// ? Loop through AI raw results
for (Object[] row : aiRawResults) {
    String subject = (String) row[0];
 
    // Time spent calculation
    double timeSpentSeconds;
    if (row[3] instanceof Number) {
        timeSpentSeconds = ((Number) row[3]).doubleValue();
    } else {
        String[] parts = row[3].toString().split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        timeSpentSeconds = hours * 3600 + minutes * 60 + seconds;
    }
 
    long chapterCount = ((Number) row[4]).longValue();
 
    // ? Fetch values from maps
    double avgMarks = subjectToAvgMarksOutOf180.getOrDefault(subject, 0.0);
    double avgPercentage = subjectToAvgPercentage.getOrDefault(subject, 0.0);
    double avgTimeSpentMinutes = subjectToAvgTimeSpent.getOrDefault(subject, 0.0);
    double avgNeg = subjectToAvgNegativeMarks.getOrDefault(subject, 0.0);
 
    double safeAvgPercentage = Math.max(0.0, avgPercentage);
 
    // ? Use query-based avgTimeSpent, fallback to calculated average
    String avgTime = avgTimeSpentMinutes > 0
        ? avgTimeSpentMinutes + " min"
        : formatDuration((long) (timeSpentSeconds / Math.max(1, chapterCount))); // avoid division by zero
 
    // ✅ Generate messages based on performance
    List<String> messages = new ArrayList<>();
    if (safeAvgPercentage >= 75) {
        messages.add("🔥 Excellent performance in " + subject + " – keep up the momentum!");
    } else if (safeAvgPercentage >= 40) {
        messages.add("📈 Fair attempt in " + subject + " – strengthen your weak zones.");
    } else {
        messages.add("⚠️ Needs attention – focus more on " + subject + " fundamentals.");
    }
 
    // ? Build DTO
    SwotNormalResponseDTO.AiInsightDTO insight = new SwotNormalResponseDTO.AiInsightDTO();
    insight.setSubject(subject);
    insight.setAvgMarks(avgMarks);
    insight.setAvgPercentage(safeAvgPercentage);
    insight.setAvgTimeSpent(avgTime);
    insight.setMessages(messages);
    insight.setAvgNegativeMarks(avgNeg);
 
    aiInsightsMap.put(subject, insight);
}
 
        

    
        Long totalSeconds = examRepository.getTotalTimeSpentByUserIdAndExamType(userId, Subject.ALL);


        if (totalSeconds == null) totalSeconds = 0L;
        String totalTime = formatDuration(totalSeconds);

        SwotNormalResponseDTO swotResponse = new SwotNormalResponseDTO();
        swotResponse.setExamType("ALL");
        swotResponse.setChapters(chapters);
        swotResponse.setSwot(swotMap);
        swotResponse.setAiInsights(aiInsightsMap);
        swotResponse.setTotalTimeSpent(totalTime);
        swotResponse.setSubmittedDateTime(new Date());
        swotResponse.setTotalMarks((int) chapters.stream().mapToDouble(SubjectWisePerformanceChapterNormalDTO::getMarks).sum());

        return swotResponse;
    }

    private String formatDuration(Long seconds) {
        if (seconds == null) return "00:00:00";
        long s = seconds;
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}
