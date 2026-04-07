 
package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.dto.MysteryBoxResponseDTO;
import com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO;
import com.brihathi.Multi_Tenant.entity.MysteryBox;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.MysteryBoxRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.service.MysteryBoxDataHolder;
import com.brihathi.Multi_Tenant.service.MysteryBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
 
@Service
public class MysteryBoxServiceImpl implements MysteryBoxService {
 
    private static final Logger logger = LoggerFactory.getLogger(MysteryBoxServiceImpl.class);
 
    @Autowired
    private MysteryBoxRepository mysteryBoxRepository;
    @Autowired
private TenantRepository tenantRepository;
 
 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private ExamResultRepository examResultRepository;
 
    @Autowired
    private MysteryBoxDataHolder mysteryBoxDataHolder;
    @Transactional
    @Override
    public void processMysteryBox(Long userId, Long examId) {
        logger.info("Processing mystery box for userId={} examId={}", userId, examId);
 
 
 
        Long tenantId = userRepository.findById(userId)
        .orElseThrow()
        .getTenantId();
 
Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
 
boolean usePublic =
        tenant == null ||
        "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());
 
        logger.error("TenantId: {}", tenantId);
logger.error("Using Public: {}", usePublic);
 
 
 
        // 1️⃣ Compute delete and insert counts BEFORE making any changes
        List<SubjectQuestionCountDTO> deleteCounts =
                mysteryBoxRepository.countQuestionsToDeleteFromMysteryBoxBySubject(userId, examId);
                List<SubjectQuestionCountDTO> insertCounts =
                usePublic
                        ? mysteryBoxRepository.countNewQuestionsPublic(userId, examId)
                        : mysteryBoxRepository.countNewQuestionsTenant(userId, examId);
       
 
        // 2️⃣ Store counts in data holder for frontend or API access
        mysteryBoxDataHolder.updateCounts(userId, deleteCounts, insertCounts);
        logger.info("Delete counts: {}", deleteCounts);
        logger.info("Insert counts: {}", insertCounts);
 
        // 3️⃣ Fetch attempted qids for this exam and user
        List<String> attemptedQids = examResultRepository.findAttemptedQids(userId, examId);
        logger.error("Attempted count: {}", attemptedQids.size());
 
 
        // 4️⃣ Delete attempted questions from MysteryBox
        if (!attemptedQids.isEmpty()) {
            int deleted = mysteryBoxRepository.deleteByUserTenantAndQids(
                userId,
                tenantId,
                attemptedQids
        );
            logger.info("Deleted {} rows from mystery_box for attempted questions", deleted);
        }
 
        // 5️⃣ Fetch unattempted questions for this exam
        List<MysteryBoxResponseDTO> unattemptedQuestions =
        usePublic
                ? mysteryBoxRepository.findUnattemptedQuestionsPublic(userId, examId)
                : mysteryBoxRepository.findUnattemptedQuestionsTenant(userId, examId);
                logger.error("Unattempted count: {}", unattemptedQuestions.size());
 
 
        if (unattemptedQuestions.isEmpty()) {
            logger.info("No unattempted questions found for userId={} examId={}", userId, examId);
            return;
        }
       
        // 6️⃣ Fetch existing qids for this user to avoid duplicates
        Set<String> existingQids = mysteryBoxRepository.findQidsByUserAndTenant(userId, tenantId);
        logger.error("Existing count: {}", existingQids.size());
 
        // 7️⃣ Prepare new MysteryBox entities for insertion
        List<MysteryBox> mysteryBoxes = unattemptedQuestions.stream()
                .filter(q -> !existingQids.contains(q.getQid()))
                .map(q -> {
                    MysteryBox mb = new MysteryBox();
                    mb.setUserId(userId);
                    mb.setExamId(examId);
                    mb.setTenantId(tenantId);
                    mb.setQid(q.getQid());
                    mb.setQuestionText(q.getQuestionText());
                    mb.setSubject(q.getSubject());
                    mb.setAnswerOption1(q.getAnswerOption1());
                    mb.setAnswerOption2(q.getAnswerOption2());
                    mb.setAnswerOption3(q.getAnswerOption3());
                    mb.setAnswerOption4(q.getAnswerOption4());
                    mb.setCorrectAnswerOption(q.getCorrectAnswerOption());
                    mb.setBriefExplanation(q.getBriefExplanation());
                    return mb;
                })
                .collect(Collectors.toList());
                logger.error("To Insert count: {}", mysteryBoxes.size());
 
        // 8️⃣ Save only new entries
        if (!mysteryBoxes.isEmpty()) {
            mysteryBoxRepository.saveAll(mysteryBoxes);
            logger.info("Inserted {} new unique mystery box entries for userId={}", mysteryBoxes.size(), userId);
        } else {
            logger.info("No new mystery box entries to insert — all qids already exist for userId={}", userId);
        }
    }
 
    @Override
    public List<MysteryBoxResponseDTO> getMysteryBoxQuestions(Long userId) {
        return mysteryBoxRepository.fetchDtosByUser(userId);
    }
 
    @Override
    public List<SubjectQuestionCountDTO> getDeleteCounts(Long userId, Long examId) {
        // Return stored counts from data holder
        return mysteryBoxDataHolder.getDeleteCounts(userId);
    }
 
    @Override
    public List<SubjectQuestionCountDTO> getInsertCounts(Long userId, Long examId) {
        // Return stored counts from data holder
        return mysteryBoxDataHolder.getInsertCounts(userId);
    }
}
 
 