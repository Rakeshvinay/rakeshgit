package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.dto.SwotResponseDTO;
import com.brihathi.Multi_Tenant.dto.SwotResponseDTO.*;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.repository.ScheduledOverallSwotRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.service.ScheduledOverallSwotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledOverallSwotServiceImpl implements ScheduledOverallSwotService {

    private final ScheduledOverallSwotRepository swotRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    @Override
    public SwotResponseDTO viewSwotDetails(Long userId, String subdomain) {

        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Long tenantId = tenant.getTenantId();

        // ================= CHAPTER PERFORMANCE =================
        List<Object[]> raw = swotRepository.aggregateChapterPerformance(userId, tenantId);
        List<ChapterPerformanceDTO> chapters = new ArrayList<>();

        for (Object[] r : raw) {
            double marks = ((Number) r[2]).doubleValue();
            double totalMarks = ((Number) r[3]).doubleValue();
            double percent = totalMarks == 0 ? 0 : (marks * 100.0) / totalMarks;

            chapters.add(new ChapterPerformanceDTO(
                    new ChapterSDTO((String) r[0], (String) r[1]),
                    percent,
                    marks,
                    totalMarks,
                    formatDuration(((Number) r[4]).longValue())
            ));
        }

        // ================= SWOT =================
        Map<String, List<ChapterPerformanceDTO>> grouped =
                chapters.stream().collect(Collectors.groupingBy(c -> c.getChapter().getSubject()));

        Map<String, SubjectSwotDTO> swotMap = new LinkedHashMap<>();

        for (String subject : grouped.keySet()) {

            List<ChapterPerformanceDTO> list = grouped.get(subject);

            List<String> strengths = list.stream()
                    .filter(c -> c.getPercentage() >= 70)
                    .sorted(Comparator.comparingDouble(ChapterPerformanceDTO::getPercentage).reversed())
                    .limit(2)
                    .map(c -> c.getChapter().getName())
                    .toList();

            List<String> lowest = list.stream()
                    .sorted(Comparator.comparingDouble(ChapterPerformanceDTO::getPercentage))
                    .limit(3)
                    .map(c -> c.getChapter().getName())
                    .toList();

            SubjectSwotDTO dto = new SubjectSwotDTO();
            dto.setSubject(subject);

            // ✅ STRENGTHS
            dto.setStrengths(new StrengthDTO(
                    subject,
                    strengths,
                    List.of("Nice work! Despite the overall score, you've done well in these chapters:")
            ));

            // ✅ WEAKNESSES
            dto.setWeaknesses(new WeaknessDTO(
                    subject,
                    lowest,
                    List.of(
                            "📌 Needs more focused attention in these chapters:",
                            "📌 Now's the time to rebuild confidence:\n📌 Prioritize targeted revision and take chapter-level practice tests to strengthen your fundamentals."
                    )
            ));

            // ✅ OPPORTUNITIES
            dto.setOpportunities(new OpportunityDTO(
                    subject,
                    lowest.stream().limit(2).toList(),
                    List.of(
                            "📌 Stay Focused. Stay Ready. NEET SWAN isn't just a platform—it's your launchpad to success.",
                            "✅ Take frequent mock tests\n✅ Review what clicks and what needs attention\n✅ Practice not until you get it right, but until you can't get it wrong.",
                            "📌 Keep pushing—you're stronger than you think. Do focus on below.",
                            "🟡 Basic difficulty selected. Keep going! Practice more Intermediate and Advanced difficulty-level mock tests to strengthen your preparation."
                    )
            ));

            // ✅ THREATS
            List<String> threatMsgs = new ArrayList<>();
            threatMsgs.add("🛑 Top Chapters with Highest Negative Marks:");
            threatMsgs.addAll(lowest);
            threatMsgs.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
            threatMsgs.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
            threatMsgs.addAll(lowest);
            threatMsgs.add("Attempt chapter-wise specific practice tests – build confidence one step at a time.");

            dto.setThreats(new ThreatDTO(subject, threatMsgs));

            swotMap.put(subject, dto);
        }

        // ================= AI INSIGHTS =================
        List<Object[]> aiRaw = swotRepository.aggregateSubjectInsights(userId, tenantId);
        Map<String, AiInsightDTO> aiMap = new LinkedHashMap<>();

        for (Object[] row : aiRaw) {
            String subject = (String) row[0];
            double marks = ((Number) row[1]).doubleValue();
            double totalMarks = ((Number) row[2]).doubleValue();
            double percent = totalMarks == 0 ? 0 : (marks * 100.0) / totalMarks;

            List<String> messages;
            if (percent >= 75) {
                messages = List.of("🔥 Excellent performance in " + subject + " – keep up the momentum!");
            } else if (percent >= 40) {
                messages = List.of("📈 Fair attempt in " + subject + " – strengthen your weak zones.");
            } else {
                messages = List.of("⚠️ Needs attention – focus more on " + subject + " fundamentals.");
            }

            aiMap.put(subject, new AiInsightDTO(
                    subject,
                    marks,
                    percent,
                    "2.0 min",
                    0,
                    messages
            ));
        }

        Long totalSeconds = swotRepository.getTotalTimeSpent(userId, tenantId);

        return SwotResponseDTO.builder()
                .examType("ALL")
                .chapters(chapters)
                .swot(swotMap)
                .aiInsights(aiMap)
                .totalTimeSpent(formatDuration(totalSeconds))
                .submittedDateTime(LocalDateTime.now())
                .totalMarks(chapters.stream().mapToDouble(ChapterPerformanceDTO::getMarks).sum())
                .build();
    }

    private String formatDuration(Long seconds) {
        if (seconds == null) return "00:00:00";
        long s = seconds;
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}
