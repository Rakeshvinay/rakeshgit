package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.dto.RetryTestDTO;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.RetryTestService;
import com.brihathi.Multi_Tenant.service.ExamService;
import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class RetryTestServiceImpl implements RetryTestService {
    private static final Logger logger = LoggerFactory.getLogger(RetryTestServiceImpl.class);

    @Autowired
    private RetryTestRepository retryTestRepository;

    @Autowired
    private QuestionPublicRepository questionPublicRepository;

    @Autowired
    private QuestionTenantRepository questionTenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private ExamService examService;
   

    // ================= GET RETRY QUESTION RESULTS =================

    @Override
    public List<Map<String, Object>> getExamQuestionResults(Long userId, Long examId) {

        List<RetryTestDTO> questionList =
                retryTestRepository.findExamQuestion(userId, examId);

        return questionList.stream()
                .collect(Collectors.groupingBy(q ->
                        q.getSubject() + "|" +
                        q.getChapterId() + "|" +
                        q.getDifficulty() + "|" +
                        q.getGrade()))
                .entrySet()
                .stream()
                .map(entry -> {

                    String[] keys = entry.getKey().split("\\|");

                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("subject", keys[0]);
                    metadata.put("chapterId", keys[1]);
                    metadata.put("difficulty", keys[2]);
                    metadata.put("grade", keys[3]);

                    List<Map<String, Object>> questions =
                            entry.getValue().stream().map(q -> {

                                Map<String, Object> qm = new HashMap<>();
                                qm.put("questionId", q.getQuestionId());
                                qm.put("questionText", q.getQuestionText());
                                qm.put("answerOption1", q.getAnswerOption1());
                                qm.put("answerOption2", q.getAnswerOption2());
                                qm.put("answerOption3", q.getAnswerOption3());
                                qm.put("answerOption4", q.getAnswerOption4());
                                qm.put("correctAnswerOption",
                                        q.getCorrectAnswerOption());

                                return qm;

                            }).collect(Collectors.toList());

                    Map<String, Object> block = new HashMap<>();
                    block.put("metadata", metadata);
                    block.put("questions", questions);

                    return block;

                }).collect(Collectors.toList());
    }

    // ================= CREATE RETRY EXAM =================

    // @Override
    // @Transactional
    // public Map<String, Object> createAndStartRetryExam( 
    //         Long userId,
    //         Subject subject,
    //         List<String> chapterIds,
    //         Difficulty difficulty,
    //         String grade,
    //         List<Object> questions) {

    //     User user = userRepository.findById(userId)
    //             .orElseThrow(() ->
    //                     new RuntimeException("User not found"));

    //     String chapterIdsString = String.join(",", chapterIds);

    //     // Calculate subjectId (3-letter code or "ALL")
    //     String subjectId = (subject == Subject.ALL) ? "ALL" : subject.name().substring(0, 3);
        
    //     // Calculate total marks (4 marks per question)
    //     int totalMarks = questions != null ? questions.size() * 4 : 0;
        
    //     // Calculate total duration (1 minute per question, minimum 30 minutes)
    //     long durationSeconds = questions != null ? Math.max(questions.size() * 60, 1800) : 1800;

    //     Exam exam = new Exam();
    //     exam.setUser(user);
    //     exam.setTenantId(user.getTenantId());
    //     exam.setExamType(subject);
    //     exam.setSubjectId(subjectId);
    //     exam.setDifficulty(difficulty);
    //     exam.setGrade(grade);
    //     exam.setChapterId(chapterIdsString);
    //     exam.setTotalMarks(totalMarks);
    //     exam.setTotalDuration(Duration.ofSeconds(durationSeconds));
    //     exam.setStatus(Exam.ExamStatus.PENDING);

    //     Exam createdExam = examRepository.save(exam);

    //     List<ExamResult> examResults = new ArrayList<>();

    //     for (Object q : questions) {

    //         ExamResult result = new ExamResult();

    //         result.setExamId(createdExam.getExamId());
    //         result.setUserId(user.getUserId());
    //         result.setTenantId(user.getTenantId());
    //         result.setAnswered(false);
    //         result.setVisited(false);
    //         result.setMarkedForReview(false);
    //         result.setDuration(Duration.ZERO);
    //         result.setAnswerOption(null);

    //         if (q instanceof QuestionPublic qp) {

    //             result.setQid(qp.getQid());
    //             result.setCorrectAnswerOption(
    //                     qp.getCorrectAnswerOption());
    //             result.setChapter(qp.getChapter());
    //             result.setSubject(
    //                     qp.getSubject() != null ?
    //                             qp.getSubject().name() : null);

    //         } else if (q instanceof QuestionTenant qt) {

    //             result.setQid(qt.getQid());
    //             result.setCorrectAnswerOption(
    //                     qt.getCorrectAnswerOption());
    //             result.setChapter(qt.getChapter());
    //             result.setSubject(
    //                     qt.getSubject() != null ?
    //                             qt.getSubject().name() : null);
    //         }

    //         examResults.add(result);
    //     }

    //     examResultRepository.saveAll(examResults);

    //     List<Map<String, Object>> simplifiedQuestions =
    //             fetchRetryQuestions(createdExam.getExamId());

    //     Map<String, Object> examMap = new HashMap<>();

    //     examMap.put("examId", createdExam.getExamId());
    //     examMap.put("userId", user.getUserId());
    //     examMap.put("subjectId", createdExam.getSubjectId());
    //     examMap.put("chapterId", createdExam.getChapterId());
    //     examMap.put("examType", createdExam.getExamType());
    //     examMap.put("difficulty", createdExam.getDifficulty());
    //     examMap.put("grade", createdExam.getGrade());
    //     examMap.put("totalMarks", createdExam.getTotalMarks());
    //     examMap.put("totalDuration",
    //             formatDuration(createdExam.getTotalDuration()));
    //     examMap.put("startDate", createdExam.getStartDate());
    //     examMap.put("endDate", createdExam.getEndDate());
    //     examMap.put("status", createdExam.getStatus());
    //     examMap.put("questions", simplifiedQuestions);

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("exam", examMap);

    //     return response;
    // }
    @Override
    @Transactional
    public Map<String, Object> createAndStartRetryExam(
            Long userId,
            Subject subject,
            List<String> chapterIds,
            Difficulty difficulty,
            String grade,
            List<Object> questions
    ) {
    
        logger.info("Starting Retry Exam for userId: {}", userId);
    
        // ===============================
        // 1️⃣ Fetch User
        // ===============================
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with ID: " + userId));
    
        // ===============================
        // 2️⃣ Resolve Question Source (PUBLIC or TENANT)
        // ===============================
        boolean usePublic = true;
    
        if (user.getTenantId() != null) {
            Tenant tenant = tenantRepository
                    .findById(user.getTenantId())
                    .orElse(null);
    
            usePublic = (tenant == null ||
                    "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable()));
        }
    
        logger.info("Resolved Question Source: {}",
                usePublic ? "PUBLIC" : "TENANT");
    
        // ===============================
        // 3️⃣ Convert Chapter IDs
        // ===============================
        String chapterIdsString =
                (chapterIds != null && !chapterIds.isEmpty())
                        ? String.join(",", chapterIds)
                        : "";
    
        // ===============================
        // 4️⃣ Create Exam
        // ===============================
        Exam exam = new Exam();
        exam.setUser(user);
        exam.setExamType(subject);
        exam.setTenantId(user.getTenantId());
        exam.setSubjectId(subject.name().substring(0, 3));
        exam.setDifficulty(difficulty);
        exam.setGrade(grade);
        exam.setChapterId(chapterIdsString);
        exam.setStatus(Exam.ExamStatus.PENDING);
    
        Exam createdExam = examService.createExam(exam);
    
        logger.info("Created ExamId: {}", createdExam.getExamId());
    
        // ===============================
        // 5️⃣ Extract QIDs from Incoming Questions
        // ===============================
        List<String> qids = questions.stream()
                .map(q -> {
                    if (q instanceof QuestionPublic qp) return qp.getQid();
                    if (q instanceof QuestionTenant qt) return qt.getQid();
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    
        if (qids.isEmpty()) {
            throw new RuntimeException("No valid question IDs provided");
        }
    
        // ===============================
        // 6️⃣ Fetch Questions From Correct Table ONLY
        // ===============================
        List<?> fullQuestions;
    
        if (usePublic) {
            fullQuestions =
                    questionPublicRepository.findByQidIn(qids);
        } else {
            fullQuestions =
                    questionTenantRepository.findByQidIn(qids);
        }
    
        if (fullQuestions.isEmpty()) {
            throw new RuntimeException("No questions found in database");
        }
    
        // ===============================
        // 7️⃣ Build ExamResults
        // ===============================
        List<ExamResult> examResults = new ArrayList<>();
    
        for (Object q : fullQuestions) {
    
            ExamResult result = new ExamResult();
    
            result.setExamId(createdExam.getExamId());
            result.setUserId(user.getUserId());
            result.setAnswered(false);
            result.setTenantId(user.getTenantId());
            result.setVisited(false);
            result.setMarkedForReview(false);
            result.setDuration(Duration.ZERO);
            result.setAnswerOption(null);
    
            if (q instanceof QuestionPublic qp) {
    
                result.setQid(qp.getQid());
                result.setCorrectAnswerOption(qp.getCorrectAnswerOption());
                result.setChapter(qp.getChapter());
                result.setSubject(qp.getSubject() != null
                        ? qp.getSubject().name()
                        : null);
    
            } else if (q instanceof QuestionTenant qt) {
    
                result.setQid(qt.getQid());
                result.setCorrectAnswerOption(qt.getCorrectAnswerOption());
                result.setChapter(qt.getChapter());
                result.setSubject(qt.getSubject() != null
                        ? qt.getSubject().name()
                        : null);
            }
    
            examResults.add(result);
        }
    
        examResultRepository.saveAll(examResults);
    
        logger.info("Saved {} exam results", examResults.size());
    
        // ===============================
        // 8️⃣ Fetch Questions For Response
        // ===============================
        List<Map<String, Object>> simplifiedQuestions =
                fetchRetryQuestions(createdExam.getExamId());
    
        // ===============================
        // 9️⃣ Prepare Response
        // ===============================
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", user.getUserId());
        userMap.put("name", user.getName());
        userMap.put("phoneNumber", user.getPhoneNumber());
        userMap.put("createdAt", user.getCreatedAt());
        userMap.put("updatedAt", user.getUpdatedAt());
    
        Map<String, Object> examMap = new HashMap<>();
        examMap.put("examId", createdExam.getExamId());
        examMap.put("userId", user.getUserId());
        examMap.put("subjectId", createdExam.getSubjectId());
        examMap.put("chapterId", createdExam.getChapterId());
        examMap.put("examType", createdExam.getExamType());
        examMap.put("difficulty", createdExam.getDifficulty());
        examMap.put("grade", createdExam.getGrade());
        examMap.put("totalMarks", createdExam.getTotalMarks());
        examMap.put("totalDuration",
                formatDuration(createdExam.getTotalDuration()));
        examMap.put("startDate", createdExam.getStartDate());
        examMap.put("endDate", createdExam.getEndDate());
        examMap.put("status", createdExam.getStatus());
        examMap.put("questions", simplifiedQuestions);
    
        Map<String, Object> response = new HashMap<>();
        response.put("exam", examMap);
        response.put("user", userMap);
    
        logger.info("Retry exam created successfully.");
    
        return response;
    }
    
    // ================= FETCH QUESTIONS =================

    public List<Map<String, Object>> fetchRetryQuestions(Long examId) {

        List<ExamResult> examResults = examResultRepository.findByExamId(examId);
        
        if (examResults.isEmpty()) return Collections.emptyList();
// List<QuestionPublic> publicQs = new ArrayList<>();
// List<QuestionTenant> tenantQs = new ArrayList<>();
        List<String> qids = examResults.stream()
                .map(ExamResult::getQid)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (qids.isEmpty()) return Collections.emptyList();

        List<QuestionPublic> publicQsList =
                questionPublicRepository.findByQidIn(qids);

        List<QuestionTenant> tenantQsList =
                questionTenantRepository.findByQidIn(qids);

        List<Map<String, Object>> result = new ArrayList<>();

        for (QuestionPublic q : publicQsList) {
            result.add(mapQuestion(q));
        }

        for (QuestionTenant q : tenantQsList) {
            result.add(mapQuestion(q));
        }

        return result;
    }

    private Map<String, Object> mapQuestion(Object q) {

        Map<String, Object> map = new HashMap<>();

        if (q instanceof QuestionPublic qp) {

            map.put("qid", qp.getQid());
            map.put("subject", qp.getSubject());
            map.put("chapter", qp.getChapter());
            map.put("chapterId", qp.getChapterId());
            map.put("questionText", qp.getQuestionText());
            map.put("answerOption1", qp.getAnswerOption1());
            map.put("answerOption2", qp.getAnswerOption2());
            map.put("answerOption3", qp.getAnswerOption3());
            map.put("answerOption4", qp.getAnswerOption4());

        } else if (q instanceof QuestionTenant qt) {

            map.put("qid", qt.getQid());
            map.put("subject", qt.getSubject());
            map.put("chapter", qt.getChapter());
            map.put("chapterId", qt.getChapterId());
            map.put("questionText", qt.getQuestionText());
            map.put("answerOption1", qt.getAnswerOption1());
            map.put("answerOption2", qt.getAnswerOption2());
            map.put("answerOption3", qt.getAnswerOption3());
            map.put("answerOption4", qt.getAnswerOption4());
        }

        return map;
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }
}
