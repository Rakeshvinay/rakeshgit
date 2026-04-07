
package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.context.TenantContext;
import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.ErrorTrackerService;
import com.brihathi.Multi_Tenant.dto.ErrorTrackerResponseDTO;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ErrorTrackerServiceImpl implements ErrorTrackerService {

    private final ExamResultRepository resultRepo;
    private final QuestionPublicRepository questionPublicRepo;
    private final QuestionTenantRepository questionTenantRepo;
    private final ExamRepository examRepository;
    private final ErrorTrackerRepository trackerRepo;
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorTrackerServiceImpl.class);

    @Override
    @Transactional
    public void syncErrors(Long userId, Long examId) {
        logger.info("Starting error sync for userId={}, examId={}", userId, examId);
        
        // Validate input parameters
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }
        if (examId == null || examId <= 0) {
            throw new IllegalArgumentException("Invalid examId: " + examId);
        }
        
        // 1️⃣ Get tenant subdomain from context
        String tenantSubdomain = TenantContext.getTenant();
        
        // 2️⃣ Decide question source - fetch tenant from repository
        boolean usePublic = true; // Default to public
        
        if (tenantSubdomain != null) {
            // You should inject TenantRepository or a cache service here
            // For now, we'll use a simple approach
            // In production, implement caching as shown in previous examples
            usePublic = true; // Simplified - you need to implement tenant check
        }
        
        // 3️⃣ Fetch exam results
        List<ExamResult> examResults = resultRepo.findByExamIdAndUserId(examId, userId);
        if (examResults.isEmpty()) {
            logger.info("No exam results found for userId={}, examId={}", userId, examId);
            return;
        }
        
        // 4️⃣ Batch fetch all questions needed
        Map<String, Object> questionsMap = fetchQuestionsBatch(examResults, usePublic);
        
        // 5️⃣ Identify wrong and correct answers (ONCE, not in loop!)
        List<ExamResult> wrongAnswers = resultRepo.findWrong(userId, examId);
        List<ExamResult> correctAnswers = resultRepo.findRight(userId, examId);
        
        logger.debug("Analysis - Wrong answers: {}, Correct answers: {}", 
                    wrongAnswers.size(), correctAnswers.size());
        
        // 6️⃣ Remove questions that are now answered correctly
        if (!correctAnswers.isEmpty()) {
            List<String> rightQids = correctAnswers.stream()
                .map(ExamResult::getQid)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
            
            if (!rightQids.isEmpty()) {
                int deleted = trackerRepo.deleteByUserIdAndQids(userId, rightQids);
                logger.info("Removed {} correctly answered questions from error tracker", deleted);
            }
        }
        
        // 7️⃣ If no wrong answers, we're done
        if (wrongAnswers.isEmpty()) {
            logger.info("No wrong answers found for userId={}, examId={}", userId, examId);
            return;
        }
        
        // 8️⃣ Fetch exam details (ONCE, not in loop!)
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new EntityNotFoundException("Exam not found with id: " + examId));
        
        // 9️⃣ Process and save wrong answers to error tracker
        processWrongAnswers(userId, examId, wrongAnswers, questionsMap, exam);
        
        logger.info("Completed error sync for userId={}, examId={}. Processed {} wrong answers", 
                   userId, examId, wrongAnswers.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorTrackerResponseDTO> getErrors(Long userId) {
        logger.debug("Fetching error tracker data for userId={}", userId);
        
        if (userId == null) {
            return trackerRepo.fetchAllDtos();
        }
        return trackerRepo.fetchDtosByUser(userId);
    }

    /**
     * Fetches questions in batch based on exam results
     */
    private Map<String, Object> fetchQuestionsBatch(List<ExamResult> examResults, boolean usePublic) {
        // Extract unique QIDs from ALL exam results
        List<String> allQids = examResults.stream()
            .map(ExamResult::getQid)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        
        if (allQids.isEmpty()) {
            return Collections.emptyMap();
        }
        
        logger.debug("Fetching {} questions using {} table", allQids.size(), 
                    usePublic ? "PUBLIC" : "TENANT");
        
        if (usePublic) {
            List<QuestionPublic> questions = questionPublicRepo.findByQidIn(allQids);
            // Log if some questions are missing
            if (questions.size() != allQids.size()) {
                Set<String> foundQids = questions.stream()
                    .map(QuestionPublic::getQid)
                    .collect(Collectors.toSet());
                List<String> missingQids = allQids.stream()
                    .filter(qid -> !foundQids.contains(qid))
                    .collect(Collectors.toList());
                logger.warn("Missing {} questions: {}", missingQids.size(), missingQids);
            }
            return questions.stream()
                .collect(Collectors.toMap(QuestionPublic::getQid, q -> q));
        } else {
            List<QuestionTenant> questions = questionTenantRepo.findByQidIn(allQids);
            // Log if some questions are missing
            if (questions.size() != allQids.size()) {
                Set<String> foundQids = questions.stream()
                    .map(QuestionTenant::getQid)
                    .collect(Collectors.toSet());
                List<String> missingQids = allQids.stream()
                    .filter(qid -> !foundQids.contains(qid))
                    .collect(Collectors.toList());
                logger.warn("Missing {} questions: {}", missingQids.size(), missingQids);
            }
            return questions.stream()
                .collect(Collectors.toMap(QuestionTenant::getQid, q -> q));
        }
    }

    /**
     * Processes wrong answers and saves them to error tracker
     */
    private void processWrongAnswers(Long userId, Long examId, 
                                     List<ExamResult> wrongAnswers,
                                     Map<String, Object> questionsMap,
                                     Exam exam) {
        
        // Extract QIDs from wrong answers
        List<String> wrongQids = wrongAnswers.stream()
            .map(ExamResult::getQid)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        if (wrongQids.isEmpty()) {
            return;
        }
        
        // Fetch existing error tracker entries for these QIDs
        Map<String, ErrorTracker> existingEntries = trackerRepo
            .findByUserIdAndQidIn(userId, wrongQids)
            .stream()
            .collect(Collectors.toMap(ErrorTracker::getQid, entry -> entry));
        
        // Prepare entries for upsert
        List<ErrorTracker> entriesToSave = new ArrayList<>();
        for (ExamResult wrongResult : wrongAnswers) {
            String qid = wrongResult.getQid();
            if (qid == null) {
                logger.warn("Skipping wrong answer with null QID for userId={}", userId);
                continue;
            }
            
            // Get or create error tracker entry
            ErrorTracker entry = existingEntries.getOrDefault(qid, new ErrorTracker());
            
            // Get the CORRECT question for this specific QID
            Object question = questionsMap.get(qid);
            if (question != null) {
                populateQuestionData(entry, question);
            } else {
                logger.warn("Question not found for QID: {}", qid);
                // Continue processing even if question not found
                // We still want to track the error even without question details
            }
            
            // Set result data
            entry.setTenantId(wrongResult.getTenantId());
            entry.setExamId(examId);
            entry.setUserId(userId);
            entry.setQid(qid);
            entry.setAnsweredOption(wrongResult.getAnswerOption());
          
           

            entry.setCorrectAnswerOption(wrongResult.getCorrectAnswerOption());
            
            entriesToSave.add(entry);
        }
        
        // Save to database
        if (!entriesToSave.isEmpty()) {
            trackerRepo.saveAll(entriesToSave);
            logger.debug("Saved/updated {} entries in error tracker", entriesToSave.size());
        }
    }

    /**
     * Populates error tracker entry with question data
     */
    private void populateQuestionData(ErrorTracker entry, Object question) {
        if (question instanceof QuestionPublic qp) {
            entry.setQuestionText(qp.getQuestionText());
            entry.setSubject(qp.getSubject());
            entry.setAnswerOption1(qp.getAnswerOption1());
            entry.setAnswerOption2(qp.getAnswerOption2());
            entry.setAnswerOption3(qp.getAnswerOption3());
            entry.setAnswerOption4(qp.getAnswerOption4());
            entry.setBriefExplanation(qp.getBriefExplanation());
        } else if (question instanceof QuestionTenant qt) {
            entry.setQuestionText(qt.getQuestionText());
            entry.setSubject(qt.getSubject());
            entry.setAnswerOption1(qt.getAnswerOption1());
            entry.setAnswerOption2(qt.getAnswerOption2());
            entry.setAnswerOption3(qt.getAnswerOption3());
            entry.setAnswerOption4(qt.getAnswerOption4());
            entry.setBriefExplanation(qt.getBriefExplanation());
        }
    }
    
    // Optional: Add a method to check tenant configuration if needed
    private boolean shouldUsePublicQuestions(String tenantSubdomain) {
        // Implement your tenant check logic here
        // This should check if the tenant uses public or private questions
        // For now, return true as default
        return true;
    }
}