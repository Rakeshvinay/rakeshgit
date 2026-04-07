package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.dto.*;
import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.entity.ScheduledExam.ExamStatus;
import com.brihathi.Multi_Tenant.context.*;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.ChapterRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamFinalResultRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledChapterResultRepository;
import com.brihathi.Multi_Tenant.repository.EducatorScheduledExamRepository;
import com.brihathi.Multi_Tenant.repository.ScheduledExamResultRepository;
import com.brihathi.Multi_Tenant.repository.QuestionPublicRepository;
import com.brihathi.Multi_Tenant.repository.QuestionTenantRepository;
import com.brihathi.Multi_Tenant.repository.EducatorRepository;
import com.brihathi.Multi_Tenant.service.ScheduleExamService;
import com.brihathi.Multi_Tenant.service.RedisTestService;
import com.brihathi.Multi_Tenant.service.NotificationService;
import com.brihathi.Multi_Tenant.producer.ScheduledMessageProducer;
// import com.brihathi.Multi_Tenant.consumer.MessageConsumer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.time.ZoneId;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.scheduling.annotation.Scheduled;


@Service
@RequiredArgsConstructor
public class ScheduleExamServiceImpl implements ScheduleExamService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleExamServiceImpl.class);

    private final EducatorScheduledExamRepository educatorScheduledExamRepository;
    private final ScheduledExamRepository scheduledExamRepository;
    private final ScheduledExamResultRepository scheduledExamResultRepository;
    private final ScheduledExamFinalResultRepository scheduledExamFinalResultRepository;
    private final ScheduledChapterResultRepository scheduledChapterResultRepository;
    private final UserRepository userRepository;
    private final ScheduledMessageProducer messageProducer;

    private final QuestionPublicRepository questionPublicRepository;
    private final QuestionTenantRepository questionTenantRepository;
    private final ChapterRepository chapterRepository;
    private final TenantRepository tenantRepository;
    private final RedisTestService redisTestService;
    private final NotificationService notificationService;
    


    @Override
    @Transactional
    public ScheduleExamResponse createScheduledExam(
            CreateScheduledExamRequest request,
            String subdomain
    ) {

        /* ===================== 1️⃣ CONTEXT ===================== */

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

if (auth == null || !auth.isAuthenticated()) {
    throw new RuntimeException("Unauthorized");
}

Object principal = auth.getPrincipal();

if (!(principal instanceof Educator educator)) {
    throw new RuntimeException("Educator not authorized");
}

Long educatorId = educator.getEducatorId();


        // Long educatorId = request.getEducatorId();
        if (educatorId == null) {
            throw new RuntimeException("Educator ID is required");
        }

        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found for subdomain: " + subdomain)
                );

        Long tenantId = tenant.getTenantId();

        Subject subject = request.getSubject();
        Difficulty difficulty = request.getDifficulty();
        String grade = request.getGrade();

        /* ===================== 2️⃣ CREATE EDUCATOR SCHEDULE ===================== */

        EducatorScheduledExam educatorExam = new EducatorScheduledExam();
        educatorExam.setEducatorId(educatorId);
        educatorExam.setTenantId(tenantId);
        educatorExam.setBranch(request.getBranch());
        educatorExam.setBatch(request.getBatch());
        educatorExam.setExamType(subject);
        educatorExam.setDifficulty(difficulty);
        educatorExam.setGrade(grade);
        // educatorExam.setExamStatus("PENDING");
        educatorExam.setExamStatus(ScheduledExam.ExamStatus.PENDING);
        educatorExam.setScheduledDate(request.getScheduledDate());
        educatorExam.setScheduledTime(request.getScheduledTime());
        educatorExam.setCreatedAt(LocalDateTime.now());

        // ===================== EXAM END TIME CALCULATION =====================

// LocalDateTime startTime = educatorExam.getScheduledTime();

// long durationMinutes =
//         subject == Subject.ALL ? 180 : 60; // ALL = 3 hrs, SUBJECT = 1 hr

// LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

// educatorExam.setExamEndTime(endTime);
LocalDateTime startTime = educatorExam.getScheduledTime();

long examDurationMinutes;
long bufferMinutes;

if (subject == Subject.ALL) {
    examDurationMinutes = 180;   // 3 hours exam
    bufferMinutes = 60;          // +1 hour buffer
} else {
    examDurationMinutes = 60;    // 1 hour exam
    bufferMinutes = 30;          // +30 min buffer
}

LocalDateTime endTime = startTime
        .plusMinutes(examDurationMinutes + bufferMinutes);

educatorExam.setExamEndTime(endTime);

// ===================== TIME CONFLICT VALIDATION =====================

List<EducatorScheduledExam> existingExams =
        educatorScheduledExamRepository.findActiveExamsForBatch(
                tenantId,
                request.getBranch(),
                request.getBatch()
        );

LocalDateTime newExamStart = request.getScheduledTime();

for (EducatorScheduledExam existing : existingExams) {

    LocalDateTime blockEnd = calculateBlockedEndTime(existing);

    // ❌ New exam starts during blocked window
    if (!newExamStart.isBefore(existing.getScheduledTime())
            && newExamStart.isBefore(blockEnd)) {

        throw new RuntimeException(
            "Another exam is already scheduled for this batch and branch during this time window"
        );
    }
}




        educatorScheduledExamRepository.save(educatorExam);
        Long eduScheduledExamId = educatorExam.getEduScheduledExamId();

        /* ===================== 3️⃣ FETCH STUDENTS ===================== */

        List<User> students = userRepository.findByTenantIdAndBranchAndBatch(
                tenantId,
                request.getBranch(),
                request.getBatch()
        );

        if (students.isEmpty()) {
            throw new RuntimeException("No students found for given branch & batch");
        }

        /* ===================== 4️⃣ EXAM CONFIG ===================== */

        int totalMarks;
        long totalDuration;
        String chapterId;

        if (subject == Subject.ALL) {
            totalMarks = 720;
            totalDuration = 180 * 60L;
            chapterId = "ALL-ALL";
        } else {
            totalMarks = 180;
            totalDuration = 60 * 60L;
            chapterId = subject.getCode() + "-ALL";
        }

        /* ===================== 5️⃣ GENERATE QUESTIONS (WEIGHTAGE) ===================== */

        boolean usePublicQuestions =
                "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

        List<QuestionPreviewDTO> previewQuestions = new ArrayList<>();
        List<String> qids = new ArrayList<>();

        List<Subject> subjects =
                subject == Subject.ALL
                        ? List.of(
                                Subject.PHYSICS,
                                Subject.CHEMISTRY,
                                Subject.BOTANY,
                                Subject.ZOOLOGY
                        )
                        : List.of(subject);

        for (Subject sub : subjects) {

            // 🔥 EXACT OLD LOGIC: 45 QUESTIONS PER SUBJECT USING WEIGHTAGE
            List<?> questions = usePublicQuestions
                    ? getQuestionsWithWeightagePublic(sub, difficulty, grade, 45)
                    : getQuestionsWithWeightageTenant(sub, difficulty, grade, 45);

            for (Object q : questions) {

                String qid, chapter, chapId, text;
                String o1, o2, o3, o4;

                if (q instanceof QuestionPublic qp) {
                    qid = qp.getQid();
                    chapter = qp.getChapter();
                    chapId = qp.getChapterId();
                    text = qp.getQuestionText();
                    o1 = qp.getAnswerOption1();
                    o2 = qp.getAnswerOption2();
                    o3 = qp.getAnswerOption3();
                    o4 = qp.getAnswerOption4();
                } else {
                    QuestionTenant qt = (QuestionTenant) q;
                    qid = qt.getQid();
                    chapter = qt.getChapter();
                    chapId = qt.getChapterId();
                    text = qt.getQuestionText();
                    o1 = qt.getAnswerOption1();
                    o2 = qt.getAnswerOption2();
                    o3 = qt.getAnswerOption3();
                    o4 = qt.getAnswerOption4();
                }

                qids.add(qid);

                QuestionPreviewDTO dto = new QuestionPreviewDTO();
                dto.setQid(qid);
                dto.setSubject(sub.name());
                dto.setChapter(chapter);
                dto.setChapterId(chapId);
                // dto.setQuestionType("MCQ");
                dto.setQuestionText(text);
                dto.setAnswerOption1(o1);
                dto.setAnswerOption2(o2);
                dto.setAnswerOption3(o3);
                dto.setAnswerOption4(o4);

                previewQuestions.add(dto);
            }
        }

        /* ===================== 6️⃣ INSERT SCHEDULED_EXAMS ===================== */

        List<ScheduledExam> exams = students.stream().map(student -> {

            ScheduledExam exam = new ScheduledExam();
            exam.setUserId(student.getUserId());
            exam.setEduScheduledExamId(eduScheduledExamId);
            exam.setEducatorId(educatorId);
            exam.setTenantId(tenantId);
            exam.setBranchId(request.getBranch());
            exam.setBatchId(request.getBatch());
            exam.setSubjectId(subject.name());
            exam.setChapterId(chapterId);
            exam.setExamType(subject);
            exam.setDifficulty(difficulty);
            exam.setGrade(grade);
            exam.setTotalMarks(totalMarks);
            exam.setTotalDuration(totalDuration);
            exam.setQidsList(qids);
            exam.setStatus(ScheduledExam.ExamStatus.PENDING);
            exam.setCreatedAt(LocalDateTime.now());

            return exam;
        }).toList();

        scheduledExamRepository.saveAll(exams);
        for (ScheduledExam exam : exams) {
 
            System.out.println("Sending notification for userId=" + exam.getUserId());
         
            notificationService.sendExamScheduledNotification(
                    exam.getTenantId(),
                    exam.getUserId(),
                    exam.getEducatorId(),
                    educatorExam.getEduScheduledExamId(),
                    exam.getScheduledExamId(),
                    exam.getBatchId(),
                    exam.getBranchId(),
                    exam.getExamType(),
                    exam.getTotalDuration(),
                    request.getScheduledTime(),
                    request.getScheduledDate(),
                    educatorExam.getExamEndTime()
            );
        }

        /* ===================== 7️⃣ RESPONSE ===================== */

        EducatorScheduledExamResponse educatorResponse = new EducatorScheduledExamResponse();
        educatorResponse.setEduScheduledExamId(eduScheduledExamId);
        educatorResponse.setEducatorId(educatorId);
        educatorResponse.setTenantId(tenantId);
        educatorResponse.setExamType(subject);
        educatorResponse.setSubjectId(subject.name());
        educatorResponse.setDifficulty(difficulty);
        educatorResponse.setGrade(grade);
        educatorResponse.setBranch(request.getBranch());
        educatorResponse.setBatch(request.getBatch());
        educatorResponse.setScheduledDate(request.getScheduledDate());
        educatorResponse.setScheduledTime(request.getScheduledTime());
        educatorResponse.setExamStatus(ScheduledExam.ExamStatus.PENDING);
        educatorResponse.setCreatedAt(educatorExam.getCreatedAt());
        educatorResponse.setExamEndTime(educatorExam.getExamEndTime());


        ExamPreviewResponse examPreview = new ExamPreviewResponse();
        examPreview.setQuestions(previewQuestions);
        examPreview.setTotalMarks(totalMarks);
        examPreview.setTotalDuration(formatDuration(totalDuration));

        ScheduleExamResponse response = new ScheduleExamResponse();
        response.setEducatorScheduledExam(educatorResponse);
        response.setExam(examPreview);

        return response;
    }

    private LocalDateTime calculateBlockedEndTime(EducatorScheduledExam exam) {

        LocalDateTime start = exam.getScheduledTime();
        LocalDateTime end = exam.getExamEndTime();
    
        if (exam.getExamType() == Subject.ALL) {
            return end.plusHours(1);   // ALL → +1 hour buffer
        }
    
        return end.plusMinutes(30);   // SUBJECT / CHAPTER → +30 min buffer
    }
    

    @Scheduled(fixedRate = 60000) // runs every 1 minute
@Transactional
public void updateEducatorExamStatus() {

    LocalDateTime now = LocalDateTime.now();

    List<EducatorScheduledExam> exams =
            educatorScheduledExamRepository.findByExamStatus(ScheduledExam.ExamStatus.PENDING);

    if (exams.isEmpty()) {
        return;
    }

    List<EducatorScheduledExam> toUpdate = new ArrayList<>();

    for (EducatorScheduledExam exam : exams) {

        if (exam.getExamEndTime() != null &&
            now.isAfter(exam.getExamEndTime())) {

            // exam.setExamStatus("COMPLETED");
            exam.setExamStatus(ScheduledExam.ExamStatus.COMPLETED);
            toUpdate.add(exam);
        }
    }

    if (!toUpdate.isEmpty()) {
        educatorScheduledExamRepository.saveAll(toUpdate);
    }
}



@Scheduled(fixedRate = 60000) // every 1 minute
@Transactional
public void updateStudentScheduledExamStatus() {

    LocalDateTime now = LocalDateTime.now();

    // 1️⃣ Only student exams that are still NOT started
    List<ScheduledExam> pendingStudentExams =
            scheduledExamRepository.findByStatus(ScheduledExam.ExamStatus.PENDING);

    List<ScheduledExam> toUpdate = new ArrayList<>();

    for (ScheduledExam studentExam : pendingStudentExams) {

        Long eduScheduledExamId = studentExam.getEduScheduledExamId();
        if (eduScheduledExamId == null) continue;

        EducatorScheduledExam educatorExam =
                educatorScheduledExamRepository.findById(eduScheduledExamId)
                        .orElse(null);

        if (educatorExam == null) continue;

        // 2️⃣ Build educator start time
        LocalDateTime educatorStartTime = educatorExam.getScheduledTime();


        // 3️⃣ If student didn't start within 1 hour
        if (now.isAfter(educatorStartTime.plusHours(1))) {
            studentExam.setStatus(ScheduledExam.ExamStatus.UNATTEMPTED);
            studentExam.setUpdatedAt(LocalDateTime.now());
            toUpdate.add(studentExam);
        }
    }

    if (!toUpdate.isEmpty()) {
        scheduledExamRepository.saveAll(toUpdate);
    }
}



//     @Scheduled(fixedRate = 60000) // runs every 1 minute
// @Transactional
// public void updateEducatorExamStatus() {

//     LocalDateTime now = LocalDateTime.now();

//     List<EducatorScheduledExam> exams =
//             educatorScheduledExamRepository.findByExamStatus("PENDING");


//     for (EducatorScheduledExam exam : exams) {

//         if (exam.getExamEndTime() != null &&
//             now.isAfter(exam.getExamEndTime())) {

//                 exam.setExamStatus("COMPLETED");
//             // exam.setUpdatedAt(LocalDateTime.now());
//         }
//     }

//     educatorScheduledExamRepository.saveAll(exams);
// }

    private String formatDuration(long seconds) {
        return String.format(
                "%02d:%02d:%02d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60
        );
    }

    private List<QuestionPublic> getQuestionsWithWeightagePublic(
        Subject subject,
        Difficulty difficulty,
        String grade,
        int limit
) {

    List<Chapter> chapters = chapterRepository.findBySubject(subject.name());
    List<QuestionPublic> result = new ArrayList<>();

    for (Chapter chapter : chapters) {

        Integer target = chapter.getNumberOfQuestions();
        if (target == null || target <= 0) {
            continue;
        }

        List<QuestionPublic> questions =
                questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
                        subject.name(),
                        chapter.getChapterId(),
                        difficulty.name(),
                        grade,
                        target
                );

        result.addAll(questions);
    }

    if (result.size() < limit) {
        throw new RuntimeException(
                "Not enough public questions for subject " + subject +
                ". Required=" + limit + ", Found=" + result.size()
        );
    }

    return result.subList(0, limit);
}
private List<QuestionTenant> getQuestionsWithWeightageTenant(
    Subject subject,
    Difficulty difficulty,
    String grade,
    int limit
) {

List<Chapter> chapters = chapterRepository.findBySubject(subject.name());
List<QuestionTenant> result = new ArrayList<>();

for (Chapter chapter : chapters) {

    Integer target = chapter.getNumberOfQuestions();
    if (target == null || target <= 0) {
        continue;
    }

    List<QuestionTenant> questions =
            questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
                    subject.name(),
                    chapter.getChapterId(),
                    difficulty.name(),
                    grade,
                    target
            );

    result.addAll(questions);
}

if (result.size() < limit) {
    throw new RuntimeException(
            "Not enough tenant questions for subject " + subject +
            ". Required=" + limit + ", Found=" + result.size()
    );
}

return result.subList(0, limit);
}


@Override
public Map<String, Object> getQuestionByQid(String qid, String subdomain) {

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() ->
                    new RuntimeException("Tenant not found for subdomain: " + subdomain)
            );

    boolean usePublic =
            "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

    if (usePublic) {
        QuestionPublic q = questionPublicRepository.findByQid(qid)
                .orElseThrow(() ->
                        new RuntimeException("Question not found: " + qid));

                        return Map.of(
                            "qid", q.getQid(),
                            "questionText", q.getQuestionText(),
                            "options", Map.of(
                                "option1", q.getAnswerOption1(),
                                "option2", q.getAnswerOption2(),
                                "option3", q.getAnswerOption3(),
                                "option4", q.getAnswerOption4()
                            )
                        );
    }

    // TENANT QUESTIONS (tenant-scoped)
    QuestionTenant q = questionTenantRepository
            .findByQidAndTenantId(qid, tenant.getTenantId())
            .orElseThrow(() ->
                    new RuntimeException("Question not found: " + qid));

                    return Map.of(
                        "qid", q.getQid(),
                        "questionText", q.getQuestionText(),
                        "options", Map.of(
                            "option1", q.getAnswerOption1(),
                            "option2", q.getAnswerOption2(),
                            "option3", q.getAnswerOption3(),
                            "option4", q.getAnswerOption4()
                        )
                    );
}


@Override
    @Transactional
    public StartScheduledExamResponse startScheduledExam(Long userId, Long scheduledExamId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

if (auth == null || !auth.isAuthenticated()) {
    throw new RuntimeException("Unauthorized");
}

Object principal = auth.getPrincipal();

/* 1️⃣ BLOCK EDUCATOR TOKENS */
if (!(principal instanceof User user)) {
    throw new RuntimeException("Only students can start exams");
}

/* 2️⃣ JWT userId MUST MATCH URL userId */
if (!user.getUserId().equals(userId)) {
    throw new RuntimeException("User ID mismatch");
}


        ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
                .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

        if (!exam.getUserId().equals(userId)) {
            throw new RuntimeException("Exam does not belong to this student");
        }


        if (exam.getStatus() == ScheduledExam.ExamStatus.UNATTEMPTED) {
            throw new RuntimeException("Exam time window expired");
        }
        
        if (exam.getStatus() != ScheduledExam.ExamStatus.PENDING) {
            throw new RuntimeException("Exam already started or completed");
        }
       

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusSeconds(exam.getTotalDuration());

        exam.setStartDate(now);
        exam.setEndDate(endDate);
        exam.setStatus(ScheduledExam.ExamStatus.IN_PROGRESS);
        exam.setUpdatedAt(now);

        scheduledExamRepository.save(exam);

        // ===================== CREATE RESULT ROWS =====================

        Tenant tenant = tenantRepository.findById(
               exam.getTenantId()
        ).orElseThrow(() -> new RuntimeException("Tenant not found"));

        boolean usePublic = "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

        List<ScheduledExamResult> results = new ArrayList<>();

        for (String qid : exam.getQidsList()) {

            String subject;
            String chapter;
            String correctAnswer;

            if (usePublic) {
                QuestionPublic q = questionPublicRepository.findByQid(qid)
                        .orElseThrow(() -> new RuntimeException("Question not found: " + qid));
                subject = q.getSubject().name();
                chapter = q.getChapter();
                correctAnswer = q.getCorrectAnswerOption();
            } else {
                QuestionTenant q = questionTenantRepository.findByQid(qid)
                        .orElseThrow(() -> new RuntimeException("Question not found: " + qid));
                subject = q.getSubject().name();
                chapter = q.getChapter();
                correctAnswer = q.getCorrectAnswerOption();
            }

            ScheduledExamResult r = new ScheduledExamResult();
            r.setScheduledExamId(exam.getScheduledExamId());
            r.setEduScheduledExamId(exam.getEduScheduledExamId());
            r.setUserId(userId);
            r.setTenantId(exam.getTenantId());
            r.setEducatorId(exam.getEducatorId());
            r.setBranchId(exam.getBranchId());
            r.setBatchId(exam.getBatchId());
            r.setQid(qid);
            r.setSubject(subject);
            r.setChapter(chapter);
            r.setAnswered(false);
            r.setVisited(false);
            r.setMarkedForReview(false);
            r.setDuration(Duration.ZERO);
            r.setAnswerOption(null);
            r.setCorrectAnswerOption(correctAnswer);
            r.setValidateAnswer(null); // ✅ rule you defined
            r.setMarks(null);
            r.setExamType(exam.getExamType());

            results.add(r);
        }

        // ✅ FIXED: correct repository + variable
        scheduledExamResultRepository.saveAll(results);

        // ===================== LOAD INTO REDIS =====================

        redisTestService.saveExamResultsToRedis(
            exam.getTenantId(),
            userId,
            exam.getScheduledExamId(),
            results
    );
    

        // ===================== RESPONSE =====================
        EducatorScheduledExam educatorExam =
        educatorScheduledExamRepository.findById(
               exam.getEduScheduledExamId()
        ).orElseThrow(() -> new RuntimeException("Educator scheduled exam not found"));

        StartScheduledExamResponse response = new StartScheduledExamResponse();

        response.setEduScheduledExamId(
                exam.getEduScheduledExamId()
        );
        response.setScheduledExamId(exam.getScheduledExamId());
        response.setUserId(exam.getUserId());
        
        response.setScheduledDate(educatorExam.getScheduledDate());
        response.setScheduledTime(educatorExam.getScheduledTime());
        
        
        response.setTenantId(exam.getTenantId());
        response.setBatch(exam.getBatchId());
        response.setBranch(exam.getBranchId());
        
        response.setSubjectId(exam.getSubjectId());
        response.setExamType(exam.getExamType());
        response.setDifficulty(exam.getDifficulty());
        response.setGrade(exam.getGrade());
        
        response.setTotalMarks(exam.getTotalMarks());
        response.setTotalDuration(formatDuration(exam.getTotalDuration()));
        
        response.setStartDate(exam.getStartDate());
        response.setEndDate(exam.getEndDate());
        
        response.setStatus(exam.getStatus().name());
        response.setCreatedAt(exam.getCreatedAt());
        response.setUpdatedAt(exam.getUpdatedAt());
        
        return response;
        
    }

    @Override
    public Map<String, Object> getGeneratedQids(Long userId, Long scheduledExamId) {

        ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
                .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

        // Validate user ownership
        if (!exam.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to exam");
        }

        List<String> qids = exam.getQidsList();

        if (qids == null || qids.isEmpty()) {
            throw new RuntimeException("No questions generated for this exam");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("qids", qids);
        response.put("count", qids.size());
        response.put("examStatus", exam.getStatus().name());
        response.put("ScheduledexamId", exam.getScheduledExamId());

        

        return response;
    }


    @Override
public Map<String, Object> updateQuestion(
        Long userId,
        Long scheduledExamId,
        Map<String, Object> questionUpdate) {

    log.info("Update question request | userId={} | scheduledExamId={} | payload={}",
            userId, scheduledExamId, questionUpdate);

    /* ===================== 1️⃣ JWT VALIDATION ===================== */

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }

    User authenticatedUser = (User) authentication.getPrincipal();
    Long jwtUserId = authenticatedUser.getUserId();

    if (!jwtUserId.equals(userId)) {
        throw new RuntimeException("Unauthorized: JWT user mismatch");
    }

    /* ===================== 2️⃣ VALIDATE SCHEDULED EXAM ===================== */

    ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
            .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

    if (!exam.getUserId().equals(userId)) {
        throw new RuntimeException("Unauthorized: Exam does not belong to user");
    }

    if (exam.getStatus() != ExamStatus.IN_PROGRESS) {
        throw new RuntimeException(
                "Exam not in progress. Current status: " + exam.getStatus());
    }

    // Long tenantId = exam.getTenantId(); // ✅ REQUIRED FOR REDIS KEYS
    Long tenantId = Long.valueOf(exam.getTenantId());


    /* ===================== 3️⃣ LOAD REDIS EXAM RESULTS ===================== */

    List<ScheduledExamResult> examResults =
            redisTestService.getRedisTestService(
                    tenantId,
                    userId,
                    scheduledExamId
            );

    if (examResults == null || examResults.isEmpty()) {
        throw new RuntimeException(
                "No exam results found in Redis for userId " + userId);
    }

    /* ===================== 4️⃣ EXTRACT QUESTION ID ===================== */

    String questionId = (String) questionUpdate.get("questionId");
    if (questionId == null || questionId.isBlank()) {
        throw new RuntimeException("questionId is required");
    }

    /* ===================== 5️⃣ EXTRACT FIELDS (NULL SAFE) ===================== */

    Boolean visited = (Boolean) questionUpdate.get("visited");
    Boolean answered = (Boolean) questionUpdate.get("answered");
    Boolean markedForReview = (Boolean) questionUpdate.get("markedForReview");
    Integer duration = (Integer) questionUpdate.get("duration");
    String answerOption = (String) questionUpdate.get("answerOption");

    /* ===================== 6️⃣ FIND QUESTION IN REDIS ===================== */

    ScheduledExamResult questionResult = examResults.stream()
            .filter(result -> result.getQid().equals(questionId))
            .findFirst()
            .orElseThrow(() ->
                    new RuntimeException("Question not found in Redis exam results"));

    /* ===================== 7️⃣ UPDATE FLAGS ===================== */

    if (visited != null) questionResult.setVisited(visited);
    if (markedForReview != null) questionResult.setMarkedForReview(markedForReview);

    /* ===================== 8️⃣ HANDLE ANSWER OPTION ===================== */

    if (answerOption == null || answerOption.isBlank()) {
        questionResult.setAnswerOption(null);
        questionResult.setValidateAnswer(null);
        questionResult.setAnswered(false);
    } else {
        questionResult.setAnswerOption(answerOption);
        questionResult.setAnswered(true);

        if (questionResult.getCorrectAnswerOption() != null) {
            questionResult.setValidateAnswer(
                    answerOption.equals(questionResult.getCorrectAnswerOption())
                            ? "correct"
                            : "wrong"
            );
        }
    }

    /* ===================== 9️⃣ HANDLE DURATION ===================== */
// 9️⃣ Handle duration (MONOTONIC — NEVER DECREASE)
if (duration != null && duration > 0) {
    Duration newDuration = Duration.ofSeconds(duration);

    if (questionResult.getDuration() == null ||
        newDuration.compareTo(questionResult.getDuration()) > 0) {

        questionResult.setDuration(newDuration);
    }
}


    /* ===================== 🔟 SAVE BACK TO REDIS ===================== */

    redisTestService.saveExamResultsToRedis(
            tenantId,
            userId,
            scheduledExamId,
            examResults
    );

    /* ===================== 1️⃣1️⃣ BUILD RESPONSE ===================== */

    Duration totalTime =
            redisTestService.getTotalTime(
                    tenantId,
                    userId,
                    scheduledExamId
            );

            String questionDuration =
            questionResult.getDuration() != null
                    ? formatDuration(questionResult.getDuration().getSeconds())
                    : "00:00:00";
    
    String totalTimeFormatted =
            totalTime != null
                    ? formatDuration(totalTime.getSeconds())
                    : "00:00:00";
    Map<String, Object> questionStatus = new HashMap<>();
    questionStatus.put("scheduledExamId", scheduledExamId);
    questionStatus.put("qid", questionId);
    questionStatus.put("visited", questionResult.getVisited());
    questionStatus.put("answered", questionResult.getAnswered());
    questionStatus.put("markedForReview", questionResult.getMarkedForReview());
    questionStatus.put("answerOption", questionResult.getAnswerOption());
    questionStatus.put("duration", questionDuration);
    questionStatus.put("totalTimeSpent", totalTimeFormatted);

    return Map.of(
            "message", "Question updated successfully",
            "questionStatus", questionStatus
    );
}


@Override
public Map<String, Object> endScheduledExam(Long userId, Long scheduledExamId) {

    /* ===================== 1️⃣ JWT VALIDATION ===================== */

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }

    User authenticatedUser = (User) authentication.getPrincipal();
    if (!authenticatedUser.getUserId().equals(userId)) {
        throw new RuntimeException("Unauthorized: User ID mismatch");
    }

    /* ===================== 2️⃣ FETCH & VALIDATE EXAM ===================== */

    ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
            .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

    if (!exam.getUserId().equals(userId)) {
        throw new RuntimeException("Exam does not belong to this user");
    }

    if (exam.getStatus() != ScheduledExam.ExamStatus.IN_PROGRESS) {
        throw new RuntimeException(
                "Exam not in progress. Current status: " + exam.getStatus());
    }

    Long tenantId = Long.valueOf(exam.getTenantId());

    /* ===================== 3️⃣ LOAD RESULTS FROM REDIS ===================== */

    List<ScheduledExamResult> results =
            redisTestService.getRedisTestService(
                    tenantId,
                    userId,
                    scheduledExamId
            );

    if (results == null || results.isEmpty()) {
        throw new RuntimeException("No exam results found in Redis");
    }

    /* ===================== 4️⃣ TOTAL TIME ===================== */

    Duration totalTimeSpent =
            redisTestService.getTotalTime(
                    tenantId,
                    userId,
                    scheduledExamId
            );

    if (totalTimeSpent == null || totalTimeSpent.isZero()) {
        totalTimeSpent = results.stream()
                .map(r -> r.getDuration() != null ? r.getDuration() : Duration.ZERO)
                .reduce(Duration.ZERO, Duration::plus);
    }

    /* ===================== 5️⃣ CALCULATE STATISTICS ===================== */

    long totalQuestions = results.size();

    long answered =
            results.stream().filter(ScheduledExamResult::getAnswered).count();

    long notAnswered = totalQuestions - answered;

    long markedForReview =
            results.stream().filter(ScheduledExamResult::getMarkedForReview).count();

    long notVisited =
            results.stream().filter(r -> !r.getVisited()).count();

    long answeredAndMarkedForReview =
            results.stream()
                    .filter(r -> r.getAnswered() && r.getMarkedForReview())
                    .count();

    long visitedAndNotAnswered =
            results.stream()
                    .filter(r -> r.getVisited() && !r.getAnswered())
                    .count();

    /* ===================== 6️⃣ MARKS LOGIC ===================== */

    int marksPerQuestion = 4;
    int negativeMarks = -1;

    long correct =
            results.stream()
                    .filter(r -> "correct".equalsIgnoreCase(r.getValidateAnswer()))
                    .count();

    long wrong =
            results.stream()
                    .filter(r -> "wrong".equalsIgnoreCase(r.getValidateAnswer()))
                    .count();

    long maxPossibleMarks = totalQuestions * marksPerQuestion;

    /* ===================== 7️⃣ RESPONSE ===================== */

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("userId", userId);
    response.put("scheduledexamId", scheduledExamId);

    response.put("totalQuestions", totalQuestions);
    response.put("answered", answered);
    response.put("notAnswered", notAnswered);

    response.put("markedForReview", markedForReview);
    response.put("answeredAndMarkedForReview", answeredAndMarkedForReview);

    response.put("notVisited", notVisited);
    response.put("visitedAndNotAnswered", visitedAndNotAnswered);

    response.put("maxPossibleMarks", maxPossibleMarks);

    response.put("totalTimeSpentSeconds", totalTimeSpent.toSeconds());
    response.put("totalTimeSpent", formatDuration(totalTimeSpent.getSeconds()));

    response.put("message", "Exam summary generated with time calculation");

    return response;
}


@Override
@Transactional
public ScheduledExam abortScheduledExam(Long userId, Long scheduledExamId) {

    /* ===================== 1️⃣ JWT VALIDATION ===================== */

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }

    User authenticatedUser = (User) authentication.getPrincipal();
    if (!authenticatedUser.getUserId().equals(userId)) {
        throw new RuntimeException("Unauthorized: User ID mismatch");
    }

    /* ===================== 2️⃣ FETCH & VALIDATE EXAM ===================== */

    ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
            .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

    if (!exam.getUserId().equals(userId)) {
        throw new RuntimeException("Exam does not belong to this user");
    }

    if (exam.getStatus() != ScheduledExam.ExamStatus.IN_PROGRESS) {
        throw new RuntimeException(
                "Scheduled exam cannot be aborted. Current status: " + exam.getStatus());
    }

    /* ===================== 3️⃣ UPDATE EXAM STATUS ===================== */

    LocalDateTime now = LocalDateTime.now();
    // exam.setEndDate(now);
    exam.setStatus(ScheduledExam.ExamStatus.ABORTED);
    exam.setUpdatedAt(now);

    ScheduledExam savedExam = scheduledExamRepository.save(exam);

    /* ===================== 4️⃣ DELETE REDIS DATA ===================== */

    Long tenantId = Long.valueOf(exam.getTenantId());

    redisTestService.deleteExamResultsFromRedis(
            tenantId,
            userId,
            scheduledExamId
    );

    return savedExam;
}


@Override
@Transactional
public Map<String, Object> confirmSubmission(Long userId, Long scheduledExamId) {

    /* ===================== 1️⃣ AUTH ===================== */

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    User user = (User) auth.getPrincipal();
    if (!user.getUserId().equals(userId)) {
        throw new RuntimeException("User ID mismatch");
    }

    /* ===================== 2️⃣ EXAM ===================== */

    ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
            .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

    if (!exam.getUserId().equals(userId)) {
        throw new RuntimeException("Exam does not belong to user");
    }

    if (exam.getStatus() != ScheduledExam.ExamStatus.IN_PROGRESS) {
        throw new RuntimeException("Exam is not in progress");
    }

    Long tenantId = exam.getTenantId();

    /* ===================== 3️⃣ READ REDIS ===================== */

    List<ScheduledExamResult> redisResults =
            redisTestService.getRedisTestService(tenantId, userId, scheduledExamId);

    if (redisResults == null || redisResults.isEmpty()) {
        throw new RuntimeException("No exam results found in Redis");
    }

    /* ===================== 4️⃣ TOTAL TIME ===================== */

    Duration totalTime =
            redisTestService.getTotalTime(tenantId, userId, scheduledExamId);

    if (totalTime == null || totalTime.isZero()) {
        totalTime = redisResults.stream()
                .map(r -> r.getDuration() != null ? r.getDuration() : Duration.ZERO)
                .reduce(Duration.ZERO, Duration::plus);
    }

    /* ===================== 5️⃣ SAVE QUESTION RESULTS (DB) ===================== */

    int totalMarks = 0;

    for (ScheduledExamResult r : redisResults) {

        // if (r.getAnswerOption() == null) {
        //     r.setMarks(0);
        //     r.setValidateAnswer(null);
        // } else if (r.getAnswerOption().equals(r.getCorrectAnswerOption())) {
        //     r.setMarks(4);
        //     r.setValidateAnswer("correct");
        //     totalMarks += 4;
        // } else {
        //     r.setMarks(-1);
        //     r.setValidateAnswer("wrong");
        //     totalMarks -= 1;
        // }
        String correctAnswer;

        Tenant tenant = tenantRepository.findById(
            exam.getTenantId()
    ).orElseThrow(() -> new RuntimeException("Tenant not found"));
        boolean usePublic = "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

if (usePublic) {
    correctAnswer = questionPublicRepository
            .findByQid(r.getQid())
            .orElseThrow()
            .getCorrectAnswerOption();
} else {
    correctAnswer = questionTenantRepository
            .findByQid(r.getQid())
            .orElseThrow()
            .getCorrectAnswerOption();
}

// ALWAYS SET IT EXPLICITLY
r.setCorrectAnswerOption(correctAnswer);

if (r.getAnswerOption() == null) {
    r.setMarks(0);
    r.setValidateAnswer(null);
} else if (r.getAnswerOption().equals(correctAnswer)) {
    r.setMarks(4);
    r.setValidateAnswer("correct");
    totalMarks += 4;
} else {
    r.setMarks(-1);
    r.setValidateAnswer("wrong");
    totalMarks -= 1;
}

    }

    scheduledExamResultRepository.saveAll(redisResults);

    /* ===================== 6️⃣ DELETE REDIS ===================== */

    redisTestService.deleteExamResultsFromRedis(tenantId, userId, scheduledExamId);

    /* ===================== 7️⃣ FETCH FROM DB ===================== */

    List<ScheduledExamResult> dbResults =
            scheduledExamResultRepository.findByScheduledExamId(scheduledExamId);

    if (dbResults.isEmpty()) {
        throw new RuntimeException("No exam results found in DB");
    }

    /* ===================== 8️⃣ FINAL RESULT TABLE ===================== */

    ScheduledExamFinalResult finalResult = new ScheduledExamFinalResult();
    finalResult.setScheduledExamId(scheduledExamId);
    finalResult.setEduScheduledExamId(exam.getEduScheduledExamId());
    finalResult.setUserId(userId);
    finalResult.setTenantId(tenantId);
    finalResult.setEducatorId(exam.getEducatorId());
    finalResult.setBranchId(exam.getBranchId());
    finalResult.setBatchId(exam.getBatchId());
    finalResult.setTotalTimeSpent(formatDurationn(totalTime));
    finalResult.setSubmittedDateTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
    finalResult.setTotalMarks(totalMarks);

    scheduledExamFinalResultRepository.save(finalResult);

    /* ===================== 9️⃣ CHAPTER AGGREGATION (DB ONLY) ===================== */

    Map<String, List<ScheduledExamResult>> byChapter =
            dbResults.stream()
                    .collect(Collectors.groupingBy(ScheduledExamResult::getChapter));

    List<ScheduledChapterResult> chapterResults = new ArrayList<>();

    boolean usePublic = "PUBLIC".equalsIgnoreCase(
            tenantRepository.findById(tenantId)
                    .orElseThrow().getQuestionTable()
    );

    for (Map.Entry<String, List<ScheduledExamResult>> entry : byChapter.entrySet()) {

        String chapterName = entry.getKey();
        List<ScheduledExamResult> list = entry.getValue();

        int chapterMarks = list.stream().mapToInt(ScheduledExamResult::getMarks).sum();
        int chapterTotalMarks = list.size() * 4;

        Duration chapterTime = list.stream()
                .map(r -> r.getDuration() != null ? r.getDuration() : Duration.ZERO)
                .reduce(Duration.ZERO, Duration::plus);

        double percentage =
                chapterTotalMarks > 0
                        ? Math.max(0, (double) chapterMarks / chapterTotalMarks * 100)
                        : 0;

        // 🔍 Resolve chapter + subject from question table
        String qid = list.get(0).getQid();
        String subject;
        String chapterId;

        if (usePublic) {
            QuestionPublic q = questionPublicRepository.findByQid(qid).orElseThrow();
            subject = q.getSubject().name();
            chapterId = q.getChapterId();
        } else {
            QuestionTenant q = questionTenantRepository.findByQid(qid).orElseThrow();
            subject = q.getSubject().name();
            chapterId = q.getChapterId();
        }

        ScheduledChapterResult cr = new ScheduledChapterResult();
        cr.setScheduledExamId(scheduledExamId);
        cr.setEduScheduledExamId(exam.getEduScheduledExamId());
        cr.setUserId(userId);
        cr.setTenantId(tenantId);
        cr.setEducatorId(exam.getEducatorId());
        cr.setBranchId(exam.getBranchId());
        cr.setBatchId(exam.getBatchId());
        cr.setExamType(exam.getExamType());
        cr.setChapterId(chapterId);
        cr.setChapterName(chapterName);
        cr.setSubject(subject);
        cr.setTimeSpent(formatDurationn(chapterTime));
        cr.setMarks(chapterMarks);
        cr.setTotalMarks(chapterTotalMarks);
        cr.setPercentage(percentage);
        cr.setAiAnalysis(getAiAnalysis(percentage));

        chapterResults.add(cr);
    }

    scheduledChapterResultRepository.saveAll(chapterResults);

    /* ===================== 🔟 UPDATE EXAM ===================== */

    exam.setStatus(ScheduledExam.ExamStatus.COMPLETED);
    exam.setUpdatedAt(LocalDateTime.now());
    scheduledExamRepository.save(exam);

    /* ===================== 1️⃣1️⃣ RESPONSE ===================== */

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("message", "Exam submission confirmed and results saved successfully");
    response.put("examId", scheduledExamId);
    response.put("totalMarks", totalMarks);
    response.put("resultsSavedCount", dbResults.size());
    response.put("submittedDateTime", finalResult.getSubmittedDateTime());
    response.put(
            "chapters",
            chapterResults.stream()
                    .map(this::convertChapterResultToMap)
                    .collect(Collectors.toList())
    );




     // Send messages to RabbitMQ if examType is ALL
     if (exam.getExamType() !=null) {

       
       Long eduScheduledExamId=exam.getEduScheduledExamId();
       
            // messageProducer.sendMessage(new MessageDTO(type, userId, eduScheduledExamId));
            messageProducer.sendScheduledAnalyticsMessages(userId, eduScheduledExamId,tenantId);

        
    }
    return response;
}

private Map<String, Object> convertChapterResultToMap(
    ScheduledChapterResult chapterResult
) {
Map<String, Object> chapterMap = new HashMap<>();

chapterMap.put("chapter", Map.of(
    "id", chapterResult.getChapterId(),
    "name", chapterResult.getChapterName(),
    "subject", chapterResult.getSubject()
));

chapterMap.put("timeSpent", chapterResult.getTimeSpent());
chapterMap.put("percentage", chapterResult.getPercentage());
chapterMap.put("marks", chapterResult.getMarks());
chapterMap.put("aiAnalysis", chapterResult.getAiAnalysis());
chapterMap.put("totalMarks", chapterResult.getTotalMarks());
chapterMap.put("examType", chapterResult.getExamType());

return chapterMap;
}

private Map<String, Object> convertToMap(
    ScheduledExamFinalResult result,
    ScheduledExam exam,
    List<ScheduledChapterResult> chapterResults
) {
Map<String, Object> finalResults = new HashMap<>();

finalResults.put("totalTimeSpent", result.getTotalTimeSpent());

finalResults.put(
    "chapters",
    chapterResults.stream()
        .map(this::convertScheduledChapterResultToMap)
        .collect(Collectors.toList())
);

finalResults.put("submittedDateTime", result.getSubmittedDateTime());
finalResults.put("totalMarks", result.getTotalMarks());
finalResults.put("examType", exam.getExamType());

return finalResults;
}


private Map<String, Object> convertScheduledChapterResultToMap(
    ScheduledChapterResult chapterResult
) {
Map<String, Object> chapterMap = new HashMap<>();

chapterMap.put("chapter", Map.of(
    "id", chapterResult.getChapterId(),
    "name", chapterResult.getChapterName(),
    "subject", chapterResult.getSubject()
));

chapterMap.put("timeSpent", chapterResult.getTimeSpent());
chapterMap.put("percentage", chapterResult.getPercentage());
chapterMap.put("marks", chapterResult.getMarks());
chapterMap.put("aiAnalysis", chapterResult.getAiAnalysis());
chapterMap.put("totalMarks", chapterResult.getTotalMarks());
chapterMap.put("examType", chapterResult.getExamType());

return chapterMap;
}


private String getAiAnalysis(double percentage) {
    if (percentage >= 75) {
        return "Excellent performance! Keep up the good work.";
    } else if (percentage >= 50) {
        return "Good effort. Focus on improving weak areas.";
    } else {
        return "Need Improvement. Review this chapter's concepts and practice more.";
    }
}

private String formatDurationn(Duration duration) {
    if (duration == null) {
        return "00:00:00";
    }

    long hours = duration.toHours();
    long minutes = duration.toMinutesPart();
    long seconds = duration.toSecondsPart();

    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
}

@Override
@Transactional
// (readOnly = true)
public Map<String, Object> getFinalResults(Long userId, Long scheduledExamId) {

    /* ================= AUTH ================= */

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    User user = (User) auth.getPrincipal();
    if (!user.getUserId().equals(userId)) {
        throw new RuntimeException("User ID mismatch");
    }

    /* ================= EXAM ================= */

    ScheduledExam exam = scheduledExamRepository.findById(scheduledExamId)
            .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

    if (!exam.getUserId().equals(userId)) {
        throw new RuntimeException("Exam does not belong to user");
    }

    if (exam.getStatus() != ScheduledExam.ExamStatus.COMPLETED &&
        exam.getStatus() != ScheduledExam.ExamStatus.ABORTED) {
        throw new RuntimeException("Exam not completed or aborted");
    }

    /* ================= FINAL RESULT ================= */

    ScheduledExamFinalResult finalResult =
            scheduledExamFinalResultRepository
                    .findByScheduledExamIdAndUserId(scheduledExamId, userId)
                    .orElseThrow(() ->
                            new RuntimeException("Final result not found"));

    /* ================= CHAPTER RESULTS ================= */

    List<ScheduledChapterResult> chapters =
            scheduledChapterResultRepository
                    .findByScheduledExamId(scheduledExamId);

    if (chapters.isEmpty()) {
        throw new RuntimeException("No chapter results found");
    }

    /* ================= TOTAL PERCENTAGE ================= */

    int totalMarks = finalResult.getTotalMarks();
    int maxMarks = chapters.stream()
            .mapToInt(ScheduledChapterResult::getTotalMarks)
            .sum();

    double totalPercentage =
            maxMarks > 0 ? (double) totalMarks / maxMarks * 100 : 0;

    /* ================= RESPONSE MAP ================= */

    Map<String, Object> response = new LinkedHashMap<>();

    response.put("totalTimeSpent", finalResult.getTotalTimeSpent());
    response.put(
            "chapters",
            chapters.stream()
                    .map(this::convertScheduledChapterResultToMap)
                    .toList()
    );
    response.put("submittedDateTime", finalResult.getSubmittedDateTime());
    response.put("examType", exam.getExamType());
    response.put("totalMarks", totalMarks);
    response.put("totalPercentage", Math.max(0, totalPercentage));

    /* ================= SWOT ================= */

    response.put("swot", buildSwotFromScheduledChapterResults(chapters));

    return response;
}



// private Map<String, Object> convertScheduledChapterResultToMap(
//     ScheduledChapterResult chapter
// ) {
// Map<String, Object> map = new LinkedHashMap<>();

// map.put("chapter", Map.of(
//         "id", chapter.getChapterId(),
//         "name", chapter.getChapterName(),
//         "subject", chapter.getSubject()
// ));

// map.put("timeSpent", chapter.getTimeSpent());
// map.put("percentage", chapter.getPercentage());
// map.put("examType", chapter.getExamType());
// map.put("marks", chapter.getMarks());
// map.put("aiAnalysis", chapter.getAiAnalysis());
// map.put("totalMarks", chapter.getTotalMarks());

// return map;
// }
private Map<String, Object> buildSwotFromScheduledChapterResults(
    List<ScheduledChapterResult> chapters
) {
Map<String, List<ScheduledChapterResult>> bySubject =
        chapters.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getSubject().toUpperCase()
                ));

Map<String, Object> swot = new LinkedHashMap<>();

for (String subject : bySubject.keySet()) {
    List<ScheduledChapterResult> list = bySubject.get(subject);

    Map<String, Object> subjectSwot = new LinkedHashMap<>();
    subjectSwot.put("strengths", buildStrengthMessages(list, subject));
    subjectSwot.put("weaknesses", buildWeaknessMessages(list, subject));
    subjectSwot.put("opportunities", buildOpportunityMessages(list, subject));
    subjectSwot.put("threats", buildThreatMessages(list, subject));

    swot.put(capitalize(subject), subjectSwot);
}

return swot;
}

private Map<String, Object> buildStrengthMessages(
    List<ScheduledChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildStrengths(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ScheduledChapterResult::getMarks).sum()),
        Map.of(subject, chapters.stream()
                .map(c -> parseDuration(c.getTimeSpent()))
                .reduce(Duration.ZERO, Duration::plus)),
        true
);

Object value = result.get(capitalize(subject));
return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
}


private Map<String, Object> buildWeaknessMessages(
    List<ScheduledChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildWeaknesses(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ScheduledChapterResult::getMarks).sum()),
        Map.of(subject, (int) chapters.stream().filter(c -> c.getMarks() == 0).count()),
        Map.of(subject, new HashMap<>())
);

Object value = result.get(capitalize(subject));
return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
}



private Map<String, Object> buildOpportunityMessages(
    List<ScheduledChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildOpportunities(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ScheduledChapterResult::getMarks).sum()),
        Map.of(subject, "BASIC")
);

Object value = result.get(capitalize(subject));
return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
}

private Map<String, Object> buildThreatMessages(
    List<ScheduledChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildThreats(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ScheduledChapterResult::getMarks).sum()),
        Map.of(subject, chapters.stream()
                .map(c -> parseDuration(c.getTimeSpent()))
                .reduce(Duration.ZERO, Duration::plus)),
        Map.of(subject, new HashMap<>())
);

Object value = result.get(capitalize(subject));
return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
}



// --- STRENGTHS ---
private Map<String, Object> buildStrengths(
    Map<String, List<ScheduledChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, Duration> subjectTimes,
    boolean isNegativeMarking
) {
    Map<String, Object> strengths = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ScheduledChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        Duration timeSpent = subjectTimes.getOrDefault(subject, Duration.ZERO);
        double percentage = (double) totalScore / 180 * 100;
        List<ScheduledChapterResult> sortedChapters = chapters.stream()
            .sorted(Comparator.comparingDouble(ScheduledChapterResult::getPercentage).reversed())
            .toList();
        Map<String, Object> subjectStrength = new LinkedHashMap<>();
        List<String> messages = new ArrayList<>();

        // 1. Excellent Timing + High Accuracy
        boolean goodTiming = (
            (subject.equalsIgnoreCase("BOTANY") && timeSpent.toMinutes() <= 25) ||
            (subject.equalsIgnoreCase("ZOOLOGY") && timeSpent.toMinutes() <= 25) ||
            (subject.equalsIgnoreCase("CHEMISTRY") && timeSpent.toMinutes() <= 40) ||
            (subject.equalsIgnoreCase("PHYSICS") && timeSpent.toMinutes() <= 45)
        );
        if (percentage > 80 && goodTiming) {
            messages.add("Excellent timing—completed in a short duration with highly accurate answers!");
            if (percentage > 99) {
                messages.add("You've rocked both the score and the timing!");
            }
        }
        // 2. >75%
        if (percentage > 75) {
            messages.add("Great job! You've performed well in these chapters:");
            List<String> top2 = sortedChapters.stream().limit(2).map(ch ->ch.getChapterName()
        ).toList();
            subjectStrength.put("topChapters", top2);
            if (percentage > 90) {
                messages.add("Impressive! You've made barely any mistakes.");
            }
            if (percentage > 99) {
                messages.add("Outstanding! You've absolutely rocked every chapter.");
            }
        }
        // 3. Negative Marking Disabled AND Score = 100%
        if (!isNegativeMarking && percentage == 100) {
            messages.add("Flawless! You didn't make a single mistake.");
        }
        // 4. >50%
        if (percentage > 50 && percentage <= 75) {
            messages.add("Good job! You've performed well in these chapters:");
            List<String> top3 = sortedChapters.stream().limit(3).map(ch -> ch.getChapterName()
        ).toList();
            subjectStrength.put("topChapters", top3);
        }
        // 5. <50% but any chapter >70%
        if (percentage < 50) {
            List<String> highChapters = sortedChapters.stream()
                .filter(ch -> ch.getPercentage() > 70)
                .limit(3)
                .map(ch ->ch.getChapterName()
            )
                .toList();
            if (!highChapters.isEmpty()) {
                messages.add("Nice work! Despite the overall score, you've done well in these chapters:");
                subjectStrength.put("topChapters", highChapters);
            }
        }
        if (!messages.isEmpty()) {
            subjectStrength.put("subject", capitalize(subject));
            subjectStrength.put("messages", messages);
            strengths.put(capitalize(subject), subjectStrength);
        }
    }
    return strengths;
}

// --- WEAKNESSES ---
private Map<String, Object> buildWeaknesses(
    Map<String, List<ScheduledChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, Integer> subjectUnansweredCounts,
    Map<String, Map<String, Integer>> chapterUnansweredCounts
) {
    Map<String, Object> weaknesses = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ScheduledChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        double percentage = (double) totalScore / 180 * 100;
        Map<String, Object> subjectWeakness = new LinkedHashMap<>();
        List<String> messages = new ArrayList<>();
        List<String> lowestChapters = chapters.stream()
            .sorted(Comparator.comparingDouble(ScheduledChapterResult::getPercentage))
            .limit(3)
            .map(ch ->ch.getChapterName()
        )
            .toList();

        // 1. <50% overall
        if (percentage < 50) {
            messages.add("Needs more focused attention in these chapters:");
            subjectWeakness.put("lowestChapters", lowestChapters);
            messages.add("Now's the time to rebuild confidence:\n📌 Prioritize targeted revision and take chapter-level practice tests to strengthen your fundamentals.");
        }
        // 2. >20% unanswered
        int unanswered = subjectUnansweredCounts.getOrDefault(subject, 0);
        int totalQuestions = chapters.stream().mapToInt(ScheduledChapterResult::getTotalMarks).sum() / 4;
        double unansweredRatio = totalQuestions > 0 ? (double) unanswered / totalQuestions : 0;
        if (unansweredRatio > 0.2) {
            messages.add("More than 20% of questions were left unanswered in this subject. This suggests hesitation, time management issues, or concept uncertainty.");
            messages.add("Recommendations:\n• Reattempt the full mock under timed conditions\n• Focus on strengthening weaker topics\n• Use Mark & Review strategies during timed practice");
        }
        // 3. Chapters >30% unanswered
        Map<String, Integer> chapterUnans = chapterUnansweredCounts.getOrDefault(subject, new HashMap<>());
        List<String> uncertainChapters = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : chapterUnans.entrySet()) {
            int chapterTotal = chapters.stream().filter(ch ->ch.getChapterName()
            .equals(entry.getKey()))
                .mapToInt(ScheduledChapterResult::getTotalMarks).sum() / 4;
            double chapterUnansRatio = chapterTotal > 0 ? (double) entry.getValue() / chapterTotal : 0;
            if (chapterUnansRatio > 0.3) {
                uncertainChapters.add(entry.getKey());
            }
        }
        if (!uncertainChapters.isEmpty()) {
            messages.add("The following chapters had >30% unanswered questions, indicating lack of familiarity or uncertainty:");
            subjectWeakness.put("uncertainChapters", uncertainChapters);
            messages.add("Suggestions for Improvement:\n• Review each chapter's concept\n• Attempt chapter-level topic tests with answer review mode");
        }
        if (!messages.isEmpty()) {
            subjectWeakness.put("subject", capitalize(subject));
            subjectWeakness.put("messages", messages);
            weaknesses.put(capitalize(subject), subjectWeakness);
        }
    }
    return weaknesses;
}

// --- OPPORTUNITIES ---
private Map<String, Object> buildOpportunities(
    Map<String, List<ScheduledChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, String> subjectDifficulty
) {
    Map<String, Object> opportunities = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ScheduledChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        double percentage = (double) totalScore / 180 * 100;
        Map<String, Object> subjectOpp = new LinkedHashMap<>();
        List<String> messages = new ArrayList<>();
        List<String> lowestChapters = chapters.stream()
            .sorted(Comparator.comparingDouble(ScheduledChapterResult::getPercentage))
            .limit(2)
            .map(ch ->ch.getChapterName()
        )
            .toList();

        messages.add("📌 Stay Focused. Stay Ready. NEET SWAN isn't just a platform—it's your launchpad to success.");
        messages.add("✅ Take frequent mock tests\n✅ Review what clicks and what needs attention\n✅ Practice not until you get it right, but until you can't get it wrong.");
        if (percentage > 60) {
            messages.add("Solid effort—you're on your way to mastery!");
        }
        messages.add("📌 Keep pushing—you're stronger than you think. Do focus on below.");
        messages.add("🟡 Basic difficulty selected. Keep going! Practice more Intermediate and Advanced difficulty-level mock tests to strengthen your preparation.");
        subjectOpp.put("lowestChapters", lowestChapters);

        subjectOpp.put("subject", capitalize(subject));
        subjectOpp.put("messages", messages);
        opportunities.put(capitalize(subject), subjectOpp);
    }
    return opportunities;
}

// --- THREATS ---
private Map<String, Object> buildThreats(
    Map<String, List<ScheduledChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, Duration> subjectTimes,
    Map<String, Map<String, Integer>> chapterWrongAnswers
) {
    Map<String, Object> threats = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ScheduledChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        Duration timeSpent = subjectTimes.getOrDefault(subject, Duration.ZERO);
        Map<String, Object> subjectThreat = new LinkedHashMap<>();
        List<String> messages = new ArrayList<>();
        List<String> highestNegChapters = chapters.stream()
            .sorted(Comparator.comparingInt(ScheduledChapterResult::getMarks))
            .limit(3)
            .map(ch ->ch.getChapterName()
        )
            .toList();
        List<String> lowestChapters = chapters.stream()
            .sorted(Comparator.comparingDouble(ScheduledChapterResult::getPercentage))
            .limit(2)
            .map(ch -> ch.getChapterName()
        )
            .toList();

        // Chemistry threats
        if (subject.equalsIgnoreCase("CHEMISTRY")) {
            messages.add("🚫 Top Chapters with Highest Negative Marks:");
            messages.addAll(highestNegChapters);
            messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
            messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
            messages.addAll(lowestChapters);
            messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
            if (timeSpent.toMinutes() > 65) {
                messages.add("🧪 Chemistry (Time > 65 mins)\nWork on your timing in Chemistry. Try timed chapter NEET SWAN tests and quick-recall drills to boost speed.");
            }
        }
        // Physics threats
        else if (subject.equalsIgnoreCase("PHYSICS")) {
            messages.add("🚫 Top Chapters with Highest Negative Marks:");
            messages.addAll(highestNegChapters);
            messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
            messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
            messages.addAll(lowestChapters);
            messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
            if (timeSpent.toMinutes() > 70) {
                messages.add("🎯 Physics (Time > 70 mins)\nPhysics took longer than expected. Practice more NEETSWAN under exam-like conditions to improve problem-solving speed.");
            }
        }
        // Botany threats
        else if (subject.equalsIgnoreCase("BOTANY")) {
            messages.add("🚫 Top Chapters with Highest Negative Marks:");
            messages.addAll(highestNegChapters);
            messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
            messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
            messages.addAll(lowestChapters);
            messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
            if (timeSpent.toMinutes() > 25) {
                messages.add("🌿 Botany (Time > 25 mins)\nBotany took longer than expected. Focus on improving your reading speed and concept recall for plant biology topics.");
            }
        }
        // Zoology threats
        else if (subject.equalsIgnoreCase("ZOOLOGY")) {
            messages.add("🚫 Top Chapters with Highest Negative Marks:");
            messages.addAll(highestNegChapters);
            messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
            messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
            messages.addAll(lowestChapters);
            messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
            if (timeSpent.toMinutes() > 25) {
                messages.add("🦁 Zoology (Time > 25 mins)\nZoology took longer than expected. Practice more animal biology concepts and improve your diagram interpretation skills.");
            }
        }
        
        if (!messages.isEmpty()) {
            subjectThreat.put("subject", capitalize(subject));
            subjectThreat.put("messages", messages);
            threats.put(capitalize(subject), subjectThreat);
        }
    }
    return threats;
}

// --- Helper ---
private String capitalize(String s) {
    if (s == null || s.isEmpty()) return s;
    return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
}

private Duration parseDuration(String time) {
    if (time == null || time.isBlank()) return Duration.ZERO;
    String[] p = time.split(":");
    return Duration.ofHours(Long.parseLong(p[0]))
            .plusMinutes(Long.parseLong(p[1]))
            .plusSeconds(Long.parseLong(p[2]));
}



@Override
@Transactional
// (readOnly = true)
public List<AllScheduleExamsResDTO> getAllScheduledExams(Long userId) {

    /* ========= AUTH ========= */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    User user = (User) auth.getPrincipal();
    if (!user.getUserId().equals(userId)) {
        throw new RuntimeException("User ID mismatch");
    }

    /* ========= FETCH STUDENT EXAMS ========= */
    List<ScheduledExam> exams =
            scheduledExamRepository.findByUserId(userId);

    if (exams.isEmpty()) {
        return Collections.emptyList();
    }

    List<AllScheduleExamsResDTO> response = new ArrayList<>();

    for (ScheduledExam exam : exams) {

        AllScheduleExamsResDTO dto = new AllScheduleExamsResDTO();

        /* ========= FROM ScheduledExam ========= */
        dto.setScheduledExamId(exam.getScheduledExamId());
        dto.setUserId(exam.getUserId());
        dto.setBatchId(exam.getBatchId());
        dto.setBranchId(exam.getBranchId());
        dto.setEduScheduledExamId(exam.getEduScheduledExamId());
        dto.setTenantId(exam.getTenantId());
        dto.setStatus(exam.getStatus());
        dto.setGrade(exam.getGrade());
        dto.setDifficulty(exam.getDifficulty());
        dto.setTotalMarks((long) exam.getTotalMarks());
        dto.setTotalDuration(Duration.ofSeconds(exam.getTotalDuration()));
        dto.setExamType(exam.getExamType());

        /* ========= FROM EducatorScheduledExam ========= */
        educatorScheduledExamRepository
                .findByEduScheduledExamId(
                        exam.getEduScheduledExamId()
                )
                .ifPresent(educatorExam -> {
                    dto.setScheduledDate(educatorExam.getScheduledDate());
                    dto.setScheduledTime(educatorExam.getScheduledTime());
                    dto.setExamEndTime(educatorExam.getExamEndTime());
                });

        response.add(dto);
    }

    return response;
}


// @Override
// public List<EducatorScheduledExam> findScheduledExamsByFilters(
//         String subdomain,
//         String branch,
//         String batch,
//         String examStatus
// ) {

//     // Authentication auth = SecurityContextHolder.getContext().getAuthentication();

//     // if (auth == null || !auth.isAuthenticated()) {
//     //     return List.of();
//     // }

//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();

//     if (auth == null || !auth.isAuthenticated()) {
//         throw new RuntimeException("Unauthorized");
//     }
    
//     Object principal = auth.getPrincipal();
    
//     if (!(principal instanceof Educator educator)) {
//         throw new RuntimeException("Educator not authorized");
//     }
    
//     Long educatorId = educator.getEducatorId();
    
    
//             // Long educatorId = request.getEducatorId();
//             if (educatorId == null) {
//                 throw new RuntimeException("Educator ID is required");
//             }
    
//             Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//                     .orElseThrow(() ->
//                             new RuntimeException("Tenant not found for subdomain: " + subdomain)
//                     );
    
//             Long tenantId = tenant.getTenantId();

//     return educatorScheduledExamRepository.findScheduledExamsByFilters(
//             subdomain,
//             branch,
//             batch,
//             examStatus
//     );
// }

@Override
public Optional<ScheduledExam> getScheduledExamById(Long scheduledExamId) {
 
 
     /* ========= AUTH ========= */
     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
     if (auth == null || !auth.isAuthenticated()) {
         throw new RuntimeException("Unauthorized");
     }
     Object principal = auth.getPrincipal();
     if (!(principal instanceof User user)) {
         throw new RuntimeException("User not authorized");
     }
 
    return scheduledExamRepository.findById(scheduledExamId);
}
public ScheduledExamViewDTO mapToViewDto(ScheduledExam exam) {

    ScheduledExamViewDTO dto = new ScheduledExamViewDTO();

    dto.setScheduledExamId(exam.getScheduledExamId());
    dto.setUserId(exam.getUserId());

    dto.setEduScheduledExamId(exam.getEduScheduledExamId());
    dto.setEducatorId(exam.getEducatorId());
    dto.setTenantId(exam.getTenantId());

    dto.setBranchId(exam.getBranchId());
    dto.setBatchId(exam.getBatchId());

    dto.setSubjectId(exam.getSubjectId());
    dto.setChapterId(exam.getChapterId());

    dto.setExamType(exam.getExamType().name());
    dto.setDifficulty(exam.getDifficulty().name());
    dto.setGrade(exam.getGrade());

    dto.setTotalMarks(exam.getTotalMarks());
    dto.setTotalDuration(exam.getTotalDuration());

    dto.setStartDate(exam.getStartDate());
    dto.setEndDate(exam.getEndDate());

    dto.setStatus(exam.getStatus().name());
    dto.setCreatedAt(exam.getCreatedAt());
    dto.setUpdatedAt(exam.getUpdatedAt());

    return dto;
}



@Override
@Transactional
public void cancelScheduledExam(Long eduScheduledExamId, String subdomain) {

    /* AUTH */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    Object principal = auth.getPrincipal();
    if (!(principal instanceof Educator educator)) {
        throw new RuntimeException("Educator not authorized");
    }

    Long educatorId = educator.getEducatorId();

    /* TENANT */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* FETCH EDUCATOR EXAM */
    EducatorScheduledExam educatorExam =
            educatorScheduledExamRepository
                    .findByEduScheduledExamIdAndTenantId(eduScheduledExamId, tenant.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Exam not found"));

    /* VALIDATIONS */
    if (!educatorExam.getEducatorId().equals(educatorId)) {
        throw new RuntimeException("You can cancel only your exams");
    }

    // if ("COMPLETED".equalsIgnoreCase(educatorExam.getExamStatus())) {
    //     throw new RuntimeException("Completed exam cannot be cancelled");
    // }

    // if ("CANCELLED".equalsIgnoreCase(educatorExam.getExamStatus())) {
    //     throw new RuntimeException("Exam already cancelled");
    // }
    if (educatorExam.getExamStatus() == ScheduledExam.ExamStatus.COMPLETED) {
        throw new RuntimeException("Completed exam cannot be edited");
    }
    
    if (educatorExam.getExamStatus() == ScheduledExam.ExamStatus.CANCELLED) {
        throw new RuntimeException("Cancelled exam cannot be edited");
    }

    /* UPDATE EDUCATOR EXAM */
    // educatorExam.setExamStatus("CANCELLED");
    educatorExam.setExamStatus(ScheduledExam.ExamStatus.CANCELLED);
    educatorScheduledExamRepository.save(educatorExam);

    /* UPDATE STUDENT EXAMS */
    scheduledExamRepository.cancelStudentExams(eduScheduledExamId);
}
@Override
@Transactional
public void updateScheduledExam(Long eduScheduledExamId,
                                UpdateScheduledExamRequest request,
                                String subdomain) {

    /* AUTH */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    Object principal = auth.getPrincipal();
    if (!(principal instanceof Educator educator)) {
        throw new RuntimeException("Educator not authorized");
    }

    Long educatorId = educator.getEducatorId();

    /* TENANT */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* FETCH EXAM */
    EducatorScheduledExam exam = educatorScheduledExamRepository
            .findByEduScheduledExamIdAndTenantId(eduScheduledExamId, tenant.getTenantId())
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    /* VALIDATIONS */
    if (!exam.getEducatorId().equals(educatorId)) {
        throw new RuntimeException("You can edit only your exams");
    }

    // // if ("COMPLETED".equalsIgnoreCase(exam.getExamStatus())) {
    // if (ScheduledExam.ExamStatus.COMPLETED.equalsIgnoreCase(exam.getExamStatus())) {
    //     throw new RuntimeException("Completed exam cannot be edited");
    // }

    // // if ("CANCELLED".equalsIgnoreCase(exam.getExamStatus())) {
    // if (ScheduledExam.ExamStatus.CANCELLED.equalsIgnoreCase(exam.getExamStatus())) {
    //     throw new RuntimeException("Cancelled exam cannot be edited");
    // }
    if (exam.getExamStatus() == ScheduledExam.ExamStatus.COMPLETED) {
        throw new RuntimeException("Completed exam cannot be edited");
    }
    
    if (exam.getExamStatus() == ScheduledExam.ExamStatus.CANCELLED) {
        throw new RuntimeException("Cancelled exam cannot be edited");
    }

    /* UPDATE DATE & TIME */
    exam.setScheduledDate(request.getScheduledDate());
    exam.setScheduledTime(request.getScheduledTime());

    /* RE-CALCULATE END TIME */
    long examDurationMinutes;
    long bufferMinutes;

    if (exam.getExamType() == Subject.ALL) {
        examDurationMinutes = 180;
        bufferMinutes = 60;
    } else {
        examDurationMinutes = 60;
        bufferMinutes = 30;
    }

    LocalDateTime newEndTime = request.getScheduledTime()
            .plusMinutes(examDurationMinutes + bufferMinutes);

    exam.setExamEndTime(newEndTime);

    educatorScheduledExamRepository.save(exam);
}



@Override
public List<EducatorExamListDTO> findScheduledExamsByFilters(
        String subdomain,
        String branch,
        String batch,
        String examStatus
) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth.getPrincipal() instanceof Educator educator)) {
        throw new RuntimeException("Unauthorized");
    }

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    return educatorScheduledExamRepository.findEducatorExamDashboard(
            tenant.getTenantId(),
            branch,
            batch,
            examStatus
    );
}


@Override
public ExamOverviewDTO getExamOverview(String subdomain) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth.getPrincipal() instanceof Educator educator)) {
        throw new RuntimeException("Unauthorized");
    }

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    List<Object[]> results =
            educatorScheduledExamRepository.getExamStatusCounts(tenant.getTenantId());

    long total = 0, upcoming = 0, completed = 0, cancelled = 0;

    // for (Object[] row : results) {
    //     // String status = (String) row[0];
    //     ScheduledExam.ExamStatus status = (ScheduledExam.ExamStatus) row[0];
    //     Long count = (Long) row[1];

    //     total += count;

    //     switch (status.toUpperCase()) {
    //         case "PENDING":
    //         case "ACTIVE":
    //             upcoming += count;
    //             break;
    //         case "COMPLETED":
    //             completed += count;
    //             break;
    //         case "CANCELLED":
    //             cancelled += count;
    //             break;
    //     }
    // }
    for (Object[] row : results) {

        ScheduledExam.ExamStatus status = (ScheduledExam.ExamStatus) row[0];
        Long count = (Long) row[1];
    
        total += count;
    
        switch (status) {
            case PENDING:
            case IN_PROGRESS:
                upcoming += count;
                break;
    
            case COMPLETED:
                completed += count;
                break;
    
            case CANCELLED:
                cancelled += count;
                break;
        }
    }

    return new ExamOverviewDTO(total, upcoming, completed, cancelled);
}

}
