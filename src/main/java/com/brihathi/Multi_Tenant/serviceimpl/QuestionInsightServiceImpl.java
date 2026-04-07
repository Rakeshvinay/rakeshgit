package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.dto.QuestionInsightDTO;
import com.brihathi.Multi_Tenant.dto.QuestionInsightResponseDTO;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.QuestionInsightService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionInsightServiceImpl implements QuestionInsightService {

    private final QuestionAnalyticsRepository analyticsRepo;
    private final TenantRepository tenantRepository;
    private final QuestionPublicRepository publicQuestionRepo;
    private final QuestionTenantRepository tenantQuestionRepo;

    @Override
    public QuestionInsightResponseDTO getInsights(
            String branch, String batch, String subject, HttpServletRequest request) {

        String subdomain = extractSubdomain(request);

        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        List<QuestionInsightDTO> wrong =
                buildList(analyticsRepo.findMostWrong(subject, branch, batch), tenant, "WRONG");

        List<QuestionInsightDTO> skipped =
                buildList(analyticsRepo.findMostSkipped(subject, branch, batch), tenant, "SKIPPED");

        List<QuestionInsightDTO> time =
                buildList(analyticsRepo.findMostTimeConsuming(subject, branch, batch), tenant, "TIME");

        return QuestionInsightResponseDTO.builder()
                .mostWrong(wrong)
                .mostSkipped(skipped)
                .mostTimeConsuming(time)
                .build();
    }

    // ================= BUILD LIST =================
    private List<QuestionInsightDTO> buildList(List<Object[]> raw, Tenant tenant, String type) {

        if (raw == null || raw.isEmpty()) return Collections.emptyList();

        // 1️⃣ Collect QIDs
        List<String> qids = raw.stream()
                .map(r -> (String) r[0])
                .toList();

        // 2️⃣ Fetch question details
        boolean usePublic = "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

        List<QuestionInsightDTO> questionDetails =
                usePublic
                        ? publicQuestionRepo.fetchPublicQuestions(qids)
                        : tenantQuestionRepo.fetchTenantQuestions(qids);

        Map<String, QuestionInsightDTO> questionMap =
                questionDetails.stream()
                        .collect(Collectors.toMap(QuestionInsightDTO::getQuestionId, q -> q));

        // 3️⃣ Build final list
        List<QuestionInsightDTO> list = new ArrayList<>();

        for (Object[] r : raw) {
            String qid = (String) r[0];
            QuestionInsightDTO base = questionMap.get(qid);
            if (base == null) continue;

            QuestionInsightDTO dto = QuestionInsightDTO.builder()
                    .questionId(qid)
                    .questionText(base.getQuestionText())
                    .option1(base.getOption1())
                    .option2(base.getOption2())
                    .option3(base.getOption3())
                    .option4(base.getOption4())
                    .correctAnswer(base.getCorrectAnswer())
                    .build();

            if ("WRONG".equals(type))
                dto.setWrongCount(((Number) r[1]).longValue());

            if ("SKIPPED".equals(type))
                dto.setSkippedCount(((Number) r[1]).longValue());

            if ("TIME".equals(type))
                dto.setAvgTimeSpent(((Number) r[1]).doubleValue());

            list.add(dto);
        }

        return list;
    }

    // ================= SUBDOMAIN =================
    private String extractSubdomain(HttpServletRequest request) {
        String host = request.getServerName();
        return host.split("\\.")[0];
    }
}
