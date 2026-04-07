// package com.brihathi.Multi_Tenant.serviceimpl;
 
// import com.brihathi.Multi_Tenant.entity.Exam;
// import com.brihathi.Multi_Tenant.entity.User;
// import com.brihathi.Multi_Tenant.entity.Tenant;
// import com.brihathi.Multi_Tenant.entity.Batch;
// import com.brihathi.Multi_Tenant.entity.Branch;
// import com.brihathi.Multi_Tenant.entity.ExamResult;
// import com.brihathi.Multi_Tenant.entity.QuestionPublic;
// import com.brihathi.Multi_Tenant.entity.QuestionTenant;
// import com.brihathi.Multi_Tenant.entity.Chapter;
// import com.brihathi.Multi_Tenant.entity.ExamFinalResult;
// import com.brihathi.Multi_Tenant.entity.ChapterResult;
// import com.brihathi.Multi_Tenant.repository.ExamRepository;
// import com.brihathi.Multi_Tenant.repository.UserRepository;
// import com.brihathi.Multi_Tenant.repository.TenantRepository;
// import com.brihathi.Multi_Tenant.repository.QuestionPublicRepository;
// import com.brihathi.Multi_Tenant.repository.QuestionTenantRepository;
// import com.brihathi.Multi_Tenant.repository.BatchRepository;
// import com.brihathi.Multi_Tenant.repository.BranchRepository;
// import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
// import com.brihathi.Multi_Tenant.repository.ChapterRepository;
// import com.brihathi.Multi_Tenant.repository.ExamFinalResultRepository;
// import com.brihathi.Multi_Tenant.repository.ChapterResultRepository;
// import com.brihathi.Multi_Tenant.repository.DifficultyRepository;
// import com.brihathi.Multi_Tenant.repository.GradeRepository;
// import com.brihathi.Multi_Tenant.service.ExamService;
// import com.brihathi.Multi_Tenant.enums.Subject;
// import com.brihathi.Multi_Tenant.enums.Difficulty;
// import com.brihathi.Multi_Tenant.service.RedisTestService;
// // import com.brihathi.Multi_Tenant.producer.MessageProducer;
// import com.brihathi.Multi_Tenant.dto.ExamResultMessageDTO;
// import com.brihathi.Multi_Tenant.dto.NormalExamResponse;
// import com.brihathi.Multi_Tenant.dto.NormalExamDTO;
// import com.brihathi.Multi_Tenant.dto.CreateNormalExamRequest;
// import com.brihathi.Multi_Tenant.dto.QuestionPreviewDTO;
// import com.brihathi.Multi_Tenant.dto.StartExamResponseDTO;
// import com.brihathi.Multi_Tenant.dto.UpdateQuestionRequestDTO;
// import com.brihathi.Multi_Tenant.dto.QuestionStatusDTO;
// import com.brihathi.Multi_Tenant.dto.EndExamSummaryDTO;
// import com.brihathi.Multi_Tenant.dto.ConfirmChapterAnalysisDTO;
// import com.brihathi.Multi_Tenant.dto.ConfirmChapterInfoDTO;
// import com.brihathi.Multi_Tenant.dto.ConfirmSubmissionResponseDTO;
// // import com.brihathi.Multi_Tenant.dto.MessageDTO;
// import com.brihathi.Multi_Tenant.dto.ExamFinalResultDTO;
// import com.brihathi.Multi_Tenant.dto.TenantDTO;
// import com.brihathi.Multi_Tenant.dto.AbortExamResponseDTO;
// import com.brihathi.Multi_Tenant.dto.BatchDTO;
// import com.brihathi.Multi_Tenant.dto.BranchDTO;
// import com.brihathi.Multi_Tenant.dto.ChapterResultDTO;
// import com.brihathi.Multi_Tenant.dto.UserDTO;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import jakarta.persistence.PersistenceException;
// import org.hibernate.exception.ConstraintViolationException;
 
// import java.time.LocalDateTime;
// import java.time.Duration;
// import java.util.*;
// import java.util.stream.Collectors;
// import java.time.ZonedDateTime;
// import java.time.ZoneId;

 
// @Service
// public class ExamServiceImpl implements ExamService {
//     private static final Logger logger = LoggerFactory.getLogger(ExamServiceImpl.class);
 
//     private final ExamRepository examRepository;
//     private final UserRepository userRepository;
//     private final QuestionPublicRepository questionPublicRepository;
//     private final TenantRepository tenantRepository;
//     private final QuestionTenantRepository questionTenantRepository;
//     private final ExamResultRepository examResultRepository;
//     private final RedisTestService redisExamResultService;
//     private final ChapterRepository chapterRepository;
//     private final BranchRepository branchRepository;
//     private final BatchRepository batchRepository;
//     private final ExamFinalResultRepository examFinalResultRepository;
//     private final ChapterResultRepository chapterResultRepository;
//     // private final MessageProducer messageProducer;
//     private final DifficultyRepository difficultyRepository;
//     private final GradeRepository gradeRepository;
 
//     @Autowired
//     public ExamServiceImpl(ExamRepository examRepository,
//                           UserRepository userRepository,
//                           BatchRepository batchRepository,
//                           BranchRepository branchRepository,
//                         //   QuestionRepository questionRepository,
//                         TenantRepository tenantRepository,
//                         QuestionPublicRepository questionPublicRepository,
//                         QuestionTenantRepository questionTenantRepository,
//                           ExamResultRepository examResultRepository,
//                           ChapterResultRepository chapterResultRepository,
//                           RedisTestService redisExamResultService,
//                         //   RedisTestService redisTestService,
//                           ChapterRepository chapterRepository,
//                           ExamFinalResultRepository examFinalResultRepository,
//                         //   MessageProducer messageProducer,
//                           DifficultyRepository difficultyRepository,
//                           GradeRepository gradeRepository) {
//         this.examRepository = examRepository;
//         this.userRepository = userRepository;
//         this.branchRepository = branchRepository;
//         this.batchRepository = batchRepository;
//         // this.questionRepository = questionRepository;
//         this.tenantRepository = tenantRepository;
//         this.questionPublicRepository = questionPublicRepository;
//         this.questionTenantRepository = questionTenantRepository;
//         this.examResultRepository = examResultRepository;
//         this.chapterResultRepository = chapterResultRepository;
//         this.redisExamResultService = redisExamResultService;
//         // this.redisTestService = redisTestService;
//         this.chapterRepository = chapterRepository;
//         this.examFinalResultRepository = examFinalResultRepository;
//         // this.messageProducer = messageProducer;
//         this.difficultyRepository = difficultyRepository;
//         this.gradeRepository = gradeRepository;
//     }
 

// @Override
// public Map<String, Object> getExamInfo(String subdomain) {

//     // 1️⃣ Tenant from subdomain
//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() ->
//                     new RuntimeException("Tenant not found for subdomain: " + subdomain)
//             );

//     boolean usePublicTable =
//             "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

//     // 2️⃣ Subjects logic (UNCHANGED)
//     List<Map<String, Object>> subjects = new ArrayList<>();

//     List<String> allDifficulties = difficultyRepository.findAll()
//             .stream().map(d -> d.getDifficulty()).toList();

//     List<String> allGrades = gradeRepository.findAll()
//             .stream().map(g -> g.getGrade()).toList();

   
//  List<Chapter> allChapters = chapterRepository.findAll();
//  Map<String, List<String>> subjectToChapters = new HashMap<>();
//  for (Chapter chapter : allChapters) {
//      subjectToChapters.computeIfAbsent(chapter.getSubject(), k -> new ArrayList<>()).add(chapter.getChapter());
//  }

//  // Add ALL subjects option first
//  Map<String, Object> allSubjects = new HashMap<>();
//  allSubjects.put("subject", Subject.ALL);
//  allSubjects.put("chapters", allChapters.stream().map(Chapter::getChapter).distinct().toList());
//  allSubjects.put("difficulties", allDifficulties);
//  allSubjects.put("grades", allGrades);
//  subjects.add(allSubjects);

//  // Add individual subjects
//  for (Subject subject : Subject.values()) {
//      if (subject != Subject.ALL) {
//          Map<String, Object> subjectInfo = new HashMap<>();
//          subjectInfo.put("subject", subject);
//          subjectInfo.put("chapters", subjectToChapters.getOrDefault(subject.name(), List.of()));
//          subjectInfo.put("difficulties", allDifficulties);
//          subjectInfo.put("grades", allGrades);
//          subjects.add(subjectInfo);
//      }
//     }

//    // 3️⃣ TENANT → BRANCH → BATCH (ONLY CURRENT TENANT)
// List<Map<String, Object>> tenantsResponse = new ArrayList<>();

// Map<String, Object> tenantMap = new HashMap<>();
// tenantMap.put("tenantId", tenant.getTenantId());
// tenantMap.put("tenantName", tenant.getCollegeName()); // or subdomain

// List<Map<String, Object>> branchesResponse = new ArrayList<>();

// List<Branch> branches =
//         branchRepository.findByTenantId(tenant.getTenantId());

// for (Branch branch : branches) {

//     Map<String, Object> branchMap = new HashMap<>();
//     branchMap.put("branchId", branch.getBranchId());
//     branchMap.put("branchName", branch.getBranchName());

//     List<Map<String, Object>> batchesResponse = new ArrayList<>();

//     List<Batch> batches =
//             batchRepository.findByBranchId(branch.getBranchId());

//     for (Batch batch : batches) {
//         Map<String, Object> batchMap = new HashMap<>();
//         batchMap.put("batchId", batch.getBatchId());
//         batchMap.put("batchName", batch.getBatchName());
//         batchesResponse.add(batchMap);
//     }

//     branchMap.put("batches", batchesResponse);
//     branchesResponse.add(branchMap);
// }

// tenantMap.put("branches", branchesResponse);
// tenantsResponse.add(tenantMap);


//     // 4️⃣ FINAL RESPONSE
//     Map<String, Object> response = new HashMap<>();
//     response.put("subjects", subjects);
//     response.put("tenants", tenantsResponse);

//     return response;
// }

// // ================= HELPERS =================

// private List<String> extractDistinctChapters(List<?> questions) {
//     return questions.stream()
//             .map(q -> q instanceof QuestionPublic qp ? qp.getChapter()
//                     : ((QuestionTenant) q).getChapter())
//             .distinct()
//             .sorted()
//             .toList();
// }

// private List<String> extractChaptersBySubject(List<?> questions, Subject subject) {
//     return questions.stream()
//             .filter(q -> q instanceof QuestionPublic qp
//                     ? qp.getSubject() == subject
//                     : ((QuestionTenant) q).getSubject() == subject)
//             .map(q -> q instanceof QuestionPublic qp ? qp.getChapter()
//                     : ((QuestionTenant) q).getChapter())
//             .distinct()
//             .sorted()
//             .toList();
// }

// @Override
// @Transactional
// public NormalExamResponse createNormalExam(
//         CreateNormalExamRequest request,
//         String subdomain
// ) {

//     /* ===================== AUTH ===================== */
//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//     if (auth == null || !auth.isAuthenticated()) {
//         throw new RuntimeException("Unauthorized");
//     }
//     User user = (User) auth.getPrincipal();

//     /* ===================== TENANT ===================== */
//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));
//     Long tenantId = tenant.getTenantId();

//    /* ===================== EXAM CONFIG ===================== */

// // Subject examType = request.getExamType();   // ALL / PHYSICS / CHEMISTRY / BOTANY / ZOOLOGY
// String chapterId = request.getChapterId();  // optional
// Subject subject  = request.getSubject();  // same as examType OR optional

// /* ===================== CHAPTER ID FROM CHAPTER NAME ===================== */
// // If chapterId looks like a chapter name (contains spaces or is descriptive), 
// // try to find the actual chapterId from the database
// if (chapterId != null 
//         && !chapterId.equals("ALL-ALL") 
//         && !chapterId.endsWith("-ALL")
//         && (chapterId.contains(" ") || !chapterId.contains("-"))) {  // Looks like a name, not an ID
//     // Try to find chapter by subject and chapter name
//     if (subject != null && subject != Subject.ALL) {
//         Optional<Chapter> chapterOpt = chapterRepository.findBySubjectAndChapter(
//                 subject.name(), 
//                 chapterId
//         );
//         if (chapterOpt.isPresent()) {
//             chapterId = chapterOpt.get().getChapterId();  // Replace with actual chapterId
//         }
//     }
// }

// int questions;
// Duration duration;

// /*
//  MODE DECISION LOGIC:
//  1) examType == ALL                  → ALL SUBJECT EXAM
//  2) examType != ALL && chapterId == null → SUBJECT-WISE EXAM
//  3) examType != ALL && chapterId != null → CHAPTER-WISE EXAM
// */

// /* ===================== EXAM MODE RESOLUTION ===================== */





// /* ====== 1️⃣ ALL SUBJECT EXAM ====== */
// if (subject == Subject.ALL
//         && subject == Subject.ALL
//         && "ALL-ALL".equals(chapterId)) {

//     questions = 180;
//     duration = Duration.ofHours(3);

// }

// /* ====== 2️⃣ SUBJECT-WISE EXAM ====== */
// else if (subject != Subject.ALL
//         // && subject == subject
//         && (subject.name().substring(0, 3) + "-ALL").equals(chapterId)) {

//     questions = 45;
//     duration = Duration.ofHours(1);

// }

// /* ====== 3️⃣ CHAPTER-WISE EXAM ====== */
// else if (subject != Subject.ALL
//         // && examType == subject
//         && !chapterId.endsWith("-ALL")) {

//     questions = 30;
//     duration = Duration.ofMinutes(30);

// }

// /* ====== ❌ INVALID COMBINATION ====== */
// else {
//     throw new RuntimeException(
//         "Invalid exam configuration: subject=" + subject +
//         ", chapterId=" + chapterId
//     );
// }

//     /* ===================== SUBJECT CODE ===================== */
//     String subjectCode =
//             subject.equals("ALL") ? "ALL" : subject.name().substring(0, 3);

//     /* ===================== QUESTION GENERATION ===================== */
//     boolean usePublic =
//             "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

//     List<QuestionPreviewDTO> previews = new ArrayList<>();
//     // List<?> questionsList;

//     // if ("CHAPTER".equals(subject)) {
//     //     questionsList = usePublic
//     //             ? questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
//     //                     subject.name(),
//     //                     request.getChapterId(),
//     //                     request.getDifficulty().name(),
//     //                     request.getGrade(),
//     //                     questions
//     //             )
//     //             : questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
//     //                     subject.name(),
//     //                     request.getChapterId(),
//     //                     request.getDifficulty().name(),
//     //                     request.getGrade(),
//     //                     questions
//     //             );
//     // } else {
//     //     questionsList = usePublic
//     //             ? getQuestionsWithWeightagePublic(subject, request.getDifficulty(), request.getGrade(), questions)
//     //             : getQuestionsWithWeightageTenant(subject, request.getDifficulty(), request.getGrade(), questions);
//     // }
// // /* 🔵 CHAPTER-WISE EXAM (ONLY ONE CHAPTER) */
// // if (subject != Subject.ALL
// //     // && examType == subject
// //     && !chapterId.endsWith("-ALL")) {

// // if (usePublic) {
// //     questionsList =
// //             questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
// //                     subject.name(),
// //                     chapterId,                 // 🔥 ONLY THIS CHAPTER
// //                     request.getDifficulty().name(),
// //                     request.getGrade(),
// //                     30                          // EXACT
// //             );
// // } else {
// //     questionsList =
// //             questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
// //                     subject.name(),
// //                     chapterId,                 // 🔥 ONLY THIS CHAPTER
// //                     request.getDifficulty().name(),
// //                     request.getGrade(),
// //                     30
// //             );
// // }
// // }

// // /* 🟡 SUBJECT-WISE / 🟢 ALL-SUBJECT EXAMS */
// // else {

// // if (usePublic) {
// //     questionsList =
// //             getQuestionsWithWeightagePublic(
// //                     subject,
// //                     request.getDifficulty(),
// //                     request.getGrade(),
// //                     questions
// //             );
// // } else {
// //     questionsList =
// //             getQuestionsWithWeightageTenant(
// //                     subject,
// //                     request.getDifficulty(),
// //                     request.getGrade(),
// //                     questions
// //             );
// // }
// // if (questionsList.size() < questions) {
// //     throw new RuntimeException(
// //         "Not enough questions for chapter " + chapterId +
// //         ". Required=" + questions +
// //         ", Found=" + questionsList.size()
// //     );
// // }
// // }
 
// List<Object> questionsList = new ArrayList<>();
   
// /* 🔵 CHAPTER-WISE EXAM (ONLY ONE CHAPTER) */
// if (subject != Subject.ALL && !chapterId.endsWith("-ALL")) {

// List<?> fetched = usePublic
//         ? questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
//                 subject.name(),
//                 chapterId,
//                 request.getDifficulty().name(),
//                 request.getGrade(),
//                 30
//         )
//         : questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
//                 subject.name(),
//                 chapterId,
//                 request.getDifficulty().name(),
//                 request.getGrade(),
//                 30
//         );

// questionsList.addAll((Collection<?>) fetched);
// }

// /* 🟡 SUBJECT-WISE / 🟢 ALL-SUBJECT EXAMS */
// else {

// if (subject == Subject.ALL) {

//     for (Subject sub : List.of(
//             Subject.PHYSICS,
//             Subject.CHEMISTRY,
//             Subject.BOTANY,
//             Subject.ZOOLOGY
//     )) {

//         List<?> subQuestions = usePublic
//                 ? getQuestionsWithWeightagePublic(sub, request.getDifficulty(), request.getGrade(), 45)
//                 : getQuestionsWithWeightageTenant(sub, request.getDifficulty(), request.getGrade(), 45);

//         questionsList.addAll((Collection<?>) subQuestions);
//     }

// } else {

//     List<?> fetched = usePublic
//             ? getQuestionsWithWeightagePublic(subject, request.getDifficulty(), request.getGrade(), questions)
//             : getQuestionsWithWeightageTenant(subject, request.getDifficulty(), request.getGrade(), questions);

//     questionsList.addAll((Collection<?>) fetched);
// }
// }

// /* ✅ FINAL SAFETY CHECK */
// if (questionsList.size() < questions) {
// throw new RuntimeException(
//     "Not enough questions. Required=" + questions +
//     ", Found=" + questionsList.size()
// );
// }





//     for (Object q : questionsList) {
//         previews.add(mapToPreviewDTO(q));
//     }

//     /* ===================== SAVE EXAM ===================== */
//     Exam exam = new Exam();
//     exam.setUser(user);
//     exam.setTenantId(tenantId);
//     exam.setExamType(
//             subject.equals("ALL") ? Subject.ALL : subject
//     );
//     exam.setSubjectId(subjectCode);
//     exam.setChapterId(
//             "CHAPTER".equals(subject)
//                     ? chapterId  // Use resolved chapterId (from chapterName if needed)
//                     : subjectCode + "-ALL"
//     );
//     exam.setDifficulty(request.getDifficulty());
//     exam.setGrade(request.getGrade());
//     exam.setTotalDuration(duration);
//     exam.setStatus(Exam.ExamStatus.PENDING);

//     examRepository.save(exam);

//     /* ===================== SAVE EXAM RESULTS ===================== */
//     List<ExamResult> results = new ArrayList<>();

//     for (Object q : questionsList) {
    
//         String qid;
//         String chapter;
//         String subjectName;
//         String correctAnswer;
    
//         if (q instanceof QuestionPublic qp) {
    
//             qid = qp.getQid();
//             chapter = qp.getChapter();
//             subjectName = qp.getSubject().name();
//             correctAnswer = qp.getCorrectAnswerOption(); // ✅ IMPORTANT
    
//         } else if (q instanceof QuestionTenant qt) {
    
//             qid = qt.getQid();
//             chapter = qt.getChapter();
//             subjectName = qt.getSubject().name();
//             correctAnswer = qt.getCorrectAnswerOption(); // ✅ IMPORTANT
    
//         } else {
//             throw new RuntimeException("Unknown question type");
//         }
    
//         ExamResult r = new ExamResult();
//         r.setExamId(exam.getExamId());
//         r.setUserId(user.getUserId());
//         r.setTenantId(tenantId);
    
//         r.setQid(qid);
//         r.setSubject(subjectName);
//         r.setChapter(chapter);
    
//         r.setAnswered(false);
//         r.setVisited(false);
//         r.setMarkedForReview(false);
//         r.setDuration(Duration.ZERO);
    
//         // 🔥 THIS IS THE FIX
//         r.setCorrectAnswerOption(correctAnswer);
    
//         results.add(r);
//     }
    

//     examResultRepository.saveAll(results);

//     /* ===================== RESPONSE ===================== */
//     NormalExamDTO examDTO = new NormalExamDTO(
//         exam.getExamId(),                          // Long
//         subject.name(),                           // String examType (ALL / PHYSICS / ...)
//         subjectCode,                               // String subjectId (PHY / CHE / ...)
//         request.getDifficulty(),                   // Difficulty
//         exam.getChapterId(),                       // String
//         request.getGrade(),                        // String
//         questions,                                 // Integer totalMarks ❗ (or totalQuestions if you prefer)
//         formatDuration(duration.toSeconds()),      // String totalDuration
//         exam.getStatus().name(),                   // String
//         null,                                      // startDate
//         null,                                      // endDate
//         previews                                   // List<QuestionPreviewDTO>
// );


//     UserDTO userDTO = new UserDTO(
//             user.getUserId(),
//             user.getName(),
//             tenantId,
//             user.getUpdatedAt()
//     );

//     return new NormalExamResponse(examDTO, userDTO);
// }

// private QuestionPreviewDTO mapToPreviewDTO(Object q) {

//     QuestionPreviewDTO dto = new QuestionPreviewDTO();

//     if (q instanceof QuestionPublic qp) {

//         dto.setQid(qp.getQid());
//         dto.setSubject(qp.getSubject().name());
//         dto.setChapter(qp.getChapter());
//         dto.setChapterId(qp.getChapterId());
//         // dto.setQuestionType("MCQ");
//         dto.setQuestionText(qp.getQuestionText());

//         dto.setAnswerOption1(qp.getAnswerOption1());
//         dto.setAnswerOption2(qp.getAnswerOption2());
//         dto.setAnswerOption3(qp.getAnswerOption3());
//         dto.setAnswerOption4(qp.getAnswerOption4());

//     } else if (q instanceof QuestionTenant qt) {

//         dto.setQid(qt.getQid());
//         dto.setSubject(qt.getSubject().name());
//         dto.setChapter(qt.getChapter());
//         dto.setChapterId(qt.getChapterId());
//         // dto.setQuestionType("MCQ");
//         dto.setQuestionText(qt.getQuestionText());

//         dto.setAnswerOption1(qt.getAnswerOption1());
//         dto.setAnswerOption2(qt.getAnswerOption2());
//         dto.setAnswerOption3(qt.getAnswerOption3());
//         dto.setAnswerOption4(qt.getAnswerOption4());

//     } else {
//         throw new RuntimeException("Unknown question type: " + q.getClass());
//     }

//     return dto;
// }

// private List<QuestionPublic> getQuestionsWithWeightagePublic(
//     Subject subject,
//     Difficulty difficulty,
//     String grade,
//     int limit
// ) {

// List<Chapter> chapters = chapterRepository.findBySubject(subject.name());
// List<QuestionPublic> result = new ArrayList<>();

// for (Chapter chapter : chapters) {

//     Integer target = chapter.getNumberOfQuestions();
//     if (target == null || target <= 0) {
//         continue;
//     }

//     List<QuestionPublic> questions =
//             questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
//                     subject.name(),
//                     chapter.getChapterId(),
//                     difficulty.name(),
//                     grade,
//                     target
//             );

//     result.addAll(questions);
// }

// if (result.size() < limit) {
//     throw new RuntimeException(
//             "Not enough public questions for subject " + subject +
//             ". Required=" + limit + ", Found=" + result.size()
//     );
// }

// return result.subList(0, limit);
// }
// private List<QuestionTenant> getQuestionsWithWeightageTenant(
// Subject subject,
// Difficulty difficulty,
// String grade,
// int limit
// ) {

// List<Chapter> chapters = chapterRepository.findBySubject(subject.name());
// List<QuestionTenant> result = new ArrayList<>();

// for (Chapter chapter : chapters) {

// Integer target = chapter.getNumberOfQuestions();
// if (target == null || target <= 0) {
//     continue;
// }

// List<QuestionTenant> questions =
//         questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
//                 subject.name(),
//                 chapter.getChapterId(),
//                 difficulty.name(),
//                 grade,
//                 target
//         );

// result.addAll(questions);
// }

// if (result.size() < limit) {
// throw new RuntimeException(
//         "Not enough tenant questions for subject " + subject +
//         ". Required=" + limit + ", Found=" + result.size()
// );
// }

// return result.subList(0, limit);
// }

// private String formatDuration(long seconds) {
//     long hours = seconds / 3600;
//     long minutes = (seconds % 3600) / 60;
//     long secs = seconds % 60;

//     return String.format("%02d:%02d:%02d", hours, minutes, secs);
// }




// private List<QuestionPublic> getQuestionsWithWeightagePublicForChapters(
//     Subject subject,
//     Difficulty difficulty,
//     String grade,
//     List<String> chapterIds
// ) {

// List<QuestionPublic> result = new ArrayList<>();

// for (String chapterId : chapterIds) {

//     List<QuestionPublic> questions =
//             questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
//                     subject.name(),
//                     chapterId,
//                     difficulty.name(),
//                     grade,
//                     10 // per chapter (configurable)
//             );

//     result.addAll(questions);
// }

// if (result.isEmpty()) {
//     throw new RuntimeException("No questions found for selected chapters");
// }

// Collections.shuffle(result);
// return result;
// }


// private List<QuestionTenant> getQuestionsWithWeightageTenantForChapters(
//     Subject subject,
//     Difficulty difficulty,
//     String grade,
//     List<String> chapterIds
// ) {

// List<QuestionTenant> result = new ArrayList<>();

// for (String chapterId : chapterIds) {

//     List<QuestionTenant> questions =
//             questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
//                     subject.name(),
//                     chapterId,
//                     difficulty.name(),
//                     grade,
//                     10
//             );

//     result.addAll(questions);
// }

// if (result.isEmpty()) {
//     throw new RuntimeException("No tenant questions found for selected chapters");
// }

// Collections.shuffle(result);
// return result;
// }


// @Override
// @Transactional
// public StartExamResponseDTO startNormalExam(Long examId, String subdomain) {

//     /* ===================== AUTH ===================== */
//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//     if (auth == null || !auth.isAuthenticated()) {
//         throw new RuntimeException("Unauthorized");
//     }

//     if (!(auth.getPrincipal() instanceof User user)) {
//         throw new RuntimeException("Only student can start exam");
//     }

//     /* ===================== TENANT ===================== */
//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     /* ===================== FETCH EXAM ===================== */
//     Exam exam = examRepository.findById(examId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     /* ===================== VALIDATIONS ===================== */
//     if (!exam.getUser().getUserId().equals(user.getUserId())) {
//         throw new RuntimeException("Unauthorized exam access");
//     }

//     if (!exam.getTenantId().equals(tenant.getTenantId())) {
//         throw new RuntimeException("Tenant mismatch");
//     }

//     /* ===================== STATUS HANDLING ===================== */

//     // ❌ Completed / Aborted → NOT allowed
//     if (exam.getStatus() == Exam.ExamStatus.COMPLETED
//             || exam.getStatus() == Exam.ExamStatus.ABORTED) {
//         throw new RuntimeException("Exam already completed");
//     }

//     // ✅ Already started → just return snapshot (NO ERROR)
//     if (exam.getStatus() == Exam.ExamStatus.IN_PROGRESS) {
//         return buildStartExamResponse(exam);
//     }

//     /* ===================== START EXAM (ONLY PENDING) ===================== */

//     LocalDateTime startTime = LocalDateTime.now();
//     LocalDateTime endTime = startTime.plus(exam.getTotalDuration());

//     exam.setStartDate(startTime);
//     exam.setEndDate(endTime);
//     exam.setStatus(Exam.ExamStatus.IN_PROGRESS);

//     examRepository.save(exam);

//     /* ===================== REDIS PUSH (NON-CRITICAL) ===================== */
//     try {
//         List<ExamResult> results =
//                 examResultRepository.findByExamIdAndUserIdAndTenantId(
//                         examId,
//                         user.getUserId(),
//                         tenant.getTenantId()
//                 );

//         if (!results.isEmpty()) {
//             redisExamResultService.saveNormalExamResultsToRedis(
//                     tenant.getTenantId(),
//                     user.getUserId(),
//                     exam.getExamId(),
//                     results
//             );
//         }
//     } catch (Exception e) {
//         // ❗ Redis must NEVER break exam start
//         logger.error("Redis push failed for examId {}", examId, e);
//     }

//     /* ===================== RESPONSE ===================== */
//     return buildStartExamResponse(exam);
// }



// private StartExamResponseDTO buildStartExamResponse(Exam exam) {

//     StartExamResponseDTO dto = new StartExamResponseDTO();
//     dto.setExamId(exam.getExamId());
//     dto.setSubjectId(exam.getSubjectId());
//     dto.setChapterId(exam.getChapterId());
//     dto.setExamType(exam.getExamType().name());
//     dto.setDifficulty(exam.getDifficulty());
//     dto.setGrade(exam.getGrade());
//     dto.setTotalMarks(exam.getTotalMarks());
//     dto.setTotalDuration(formatDuration(exam.getTotalDuration().toSeconds()));
//     dto.setStartDate(exam.getStartDate());
//     dto.setEndDate(exam.getEndDate());
//     dto.setStatus(exam.getStatus());
//     dto.setCreatedAt(exam.getCreatedAt());
//     dto.setUpdatedAt(exam.getUpdatedAt());

//     return dto;
// }





// @Override
// public Map<String, Object> getQuestionByQid(String qid, String subdomain) {

//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() ->
//                     new RuntimeException("Tenant not found for subdomain: " + subdomain)
//             );

//     boolean usePublic =
//             "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

//     if (usePublic) {
//         QuestionPublic q = questionPublicRepository.findByQid(qid)
//                 .orElseThrow(() ->
//                         new RuntimeException("Question not found: " + qid));

//                         return Map.of(
//                             "qid", q.getQid(),
//                             "questionText", q.getQuestionText(),
//                             "options", Map.of(
//                                 "option1", q.getAnswerOption1(),
//                                 "option2", q.getAnswerOption2(),
//                                 "option3", q.getAnswerOption3(),
//                                 "option4", q.getAnswerOption4()
//                             )
//                         );
//     }

//     // TENANT QUESTIONS (tenant-scoped)
//     QuestionTenant q = questionTenantRepository
//             .findByQidAndTenantId(qid, tenant.getTenantId())
//             .orElseThrow(() ->
//                     new RuntimeException("Question not found: " + qid));

//                     return Map.of(
//                         "qid", q.getQid(),
//                         "questionText", q.getQuestionText(),
//                         "options", Map.of(
//                             "option1", q.getAnswerOption1(),
//                             "option2", q.getAnswerOption2(),
//                             "option3", q.getAnswerOption3(),
//                             "option4", q.getAnswerOption4()
//                         )
//                     );
// }

// @Override
// public Map<String, Object> getGeneratedQids(Long examId, String subdomain) {

//     /* ===================== TENANT ===================== */
//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     /* ===================== AUTH ===================== */
//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//     if (auth == null || !auth.isAuthenticated()) {
//         throw new RuntimeException("Unauthorized");
//     }

//     if (!(auth.getPrincipal() instanceof User user)) {
//         throw new RuntimeException("Only user allowed");
//     }

//     /* ===================== EXAM ===================== */
//     Exam exam = examRepository.findById(examId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     if (!exam.getUser().getUserId().equals(user.getUserId())) {
//         throw new RuntimeException("Exam does not belong to user");
//     }

//     if (!exam.getTenantId().equals(tenant.getTenantId())) {
//         throw new RuntimeException("Tenant mismatch");
//     }

//     /* ===================== FETCH QIDS (DB) ===================== */
//     List<String> qids =
//             examResultRepository.findQidsByExamIdAndUserIdAndTenantId(
//                     examId,
//                     user.getUserId(),
//                     tenant.getTenantId()
//             );

//     return Map.of(
//             "examId", examId,
//             "qids", qids,
//             "count", qids.size(),
//             "examStatus", exam.getStatus()
//     );
// }




// @Override
// @Transactional
// public AbortExamResponseDTO abortExam(Long examId, String subdomain) {

//     /* ===================== AUTH ===================== */
//     Authentication authentication =
//             SecurityContextHolder.getContext().getAuthentication();

//     if (authentication == null || !authentication.isAuthenticated()) {
//         throw new RuntimeException("User not authenticated");
//     }

//     if (!(authentication.getPrincipal() instanceof User user)) {
//         throw new RuntimeException("Only user can abort exam");
//     }

//     /* ===================== TENANT ===================== */
//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     /* ===================== FETCH EXAM ===================== */
//     Exam exam = examRepository.findById(examId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     /* ===================== VALIDATIONS ===================== */
//     if (!exam.getUser().getUserId().equals(user.getUserId())) {
//         throw new RuntimeException("Exam does not belong to this user");
//     }

//     if (!exam.getTenantId().equals(tenant.getTenantId())) {
//         throw new RuntimeException("Tenant mismatch");
//     }

//     if (exam.getStatus() != Exam.ExamStatus.IN_PROGRESS) {
//         throw new RuntimeException(
//                 "Exam cannot be aborted. Current status: " + exam.getStatus()
//         );
//     }

//     /* ===================== ABORT EXAM ===================== */
//     exam.setStatus(Exam.ExamStatus.ABORTED);
//     Exam savedExam = examRepository.save(exam);

//     /* ===================== REDIS CLEANUP (NORMAL EXAM) ===================== */
//     try {
//         redisExamResultService.deleteNormalExamResultsFromRedis(
//                 tenant.getTenantId(),
//                 user.getUserId(),
//                 examId
//         );
//     } catch (Exception e) {
//         // Redis failure must NOT fail abort
//         logger.error("Redis cleanup failed for aborted exam {}", examId, e);
//     }

//     AbortExamResponseDTO dto = new AbortExamResponseDTO();

//     dto.setUserId(user.getUserId());
//     dto.setExamId(exam.getExamId());
//     dto.setTenantId(exam.getTenantId());
    
//     dto.setSubjectId(exam.getSubjectId());
//     dto.setChapterId(exam.getChapterId());
//     dto.setExamType(exam.getExamType());
    
//     dto.setDifficulty(exam.getDifficulty());
//     dto.setGrade(exam.getGrade());
    
//     dto.setTotalMarks(exam.getTotalMarks());
//     dto.setTotalDuration(exam.getTotalDuration().getSeconds());
    
//     dto.setStartDate(exam.getStartDate());
//     dto.setEndDate(exam.getEndDate());
    
//     dto.setStatus(exam.getStatus());
//     dto.setCreatedAt(exam.getCreatedAt());
//     dto.setUpdatedAt(exam.getUpdatedAt());
    
//     return dto;
    
// }



// @Override
// @Transactional
// public QuestionStatusDTO updateQuestion(
//         Long examId,
//         UpdateQuestionRequestDTO request,
//         String subdomain
// ) {

//     /* ===================== AUTH ===================== */
//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//     if (!(auth.getPrincipal() instanceof User user)) {
//         throw new RuntimeException("Unauthorized");
//     }

//     /* ===================== TENANT ===================== */
//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     /* ===================== EXAM ===================== */
//     Exam exam = examRepository.findById(examId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     if (!exam.getUser().getUserId().equals(user.getUserId())) {
//         throw new RuntimeException("Exam does not belong to user");
//     }

//     if (exam.getStatus() != Exam.ExamStatus.IN_PROGRESS) {
//         throw new RuntimeException("Exam not in progress");
//     }

//     /* ===================== REDIS UPDATE ===================== */
//     ExamResult result =
//             redisExamResultService.updateNormalExamQuestion(
//                     tenant.getTenantId(),
//                     user.getUserId(),
//                     examId,
//                     request
//             );

//     /* ===================== RESPONSE ===================== */
//     QuestionStatusDTO dto = new QuestionStatusDTO();
//     dto.setExamId(examId);
//     dto.setQid(result.getQid());
//     dto.setChapter(result.getChapter());
//     dto.setVisited(result.getVisited());
//     dto.setAnswered(result.getAnswered());
//     dto.setMarkedForReview(result.getMarkedForReview());
//     dto.setAnswerOption(result.getAnswerOption());
//     dto.setDuration(formatDuration(result.getDuration().toSeconds()));

//     return dto;
// }


// @Override
// @Transactional
// public EndExamSummaryDTO endNormalExam(Long examId, String subdomain) {

//     /* ===================== AUTH ===================== */
//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//     if (!(auth.getPrincipal() instanceof User user)) {
//         throw new RuntimeException("Unauthorized");
//     }

//     /* ===================== TENANT ===================== */
//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     /* ===================== EXAM ===================== */
//     Exam exam = examRepository.findById(examId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     if (!exam.getUser().getUserId().equals(user.getUserId())) {
//         throw new RuntimeException("Exam does not belong to user");
//     }

//     if (!exam.getTenantId().equals(tenant.getTenantId())) {
//         throw new RuntimeException("Tenant mismatch");
//     }

//     if (exam.getStatus() != Exam.ExamStatus.IN_PROGRESS) {
//         throw new RuntimeException("Exam is not in progress");
//     }

//     /* ===================== FETCH RESULTS ===================== */
//     List<ExamResult> results =
//             redisExamResultService.getNormalExamResultsFromRedis(
//                     tenant.getTenantId(),
//                     user.getUserId(),
//                     examId
//             );

//     if (results == null || results.isEmpty()) {
//         results = examResultRepository
//                 .findByExamIdAndUserIdAndTenantId(
//                         examId,
//                         user.getUserId(),
//                         tenant.getTenantId()
//                 );
//     }

//     int totalQuestions = results.size();

//     int answered = 0;
//     int markedForReview = 0;
//     int answeredAndMarkedForReview = 0;
//     int visited = 0;

//     for (ExamResult r : results) {

//         if (Boolean.TRUE.equals(r.getVisited())) {
//             visited++;
//         }

//         if (Boolean.TRUE.equals(r.getAnswered())) {
//             answered++;
//         }

//         if (Boolean.TRUE.equals(r.getMarkedForReview())) {
//             markedForReview++;
//         }

//         if (Boolean.TRUE.equals(r.getAnswered())
//                 && Boolean.TRUE.equals(r.getMarkedForReview())) {
//             answeredAndMarkedForReview++;
//         }
//     }

//     int notAnswered = totalQuestions - answered;
//     int notVisited = totalQuestions - visited;
//     int visitedAndNotAnswered = visited - answered;

//     /* ===================== TIME ===================== */
//     Duration totalTime =
//             redisExamResultService.getNormalExamTotalTime(
//                     tenant.getTenantId(),
//                     user.getUserId(),
//                     examId
//             );
//     long seconds = totalTime.getSeconds();

//     /* ===================== MARKS ===================== */
//     int maxPossibleMarks = exam.getTotalMarks();

   
   

//     /* ===================== RESPONSE ===================== */
//     EndExamSummaryDTO dto = new EndExamSummaryDTO();

//     dto.setUserId(user.getUserId());
//     dto.setExamId(examId);

//     dto.setTotalQuestions(totalQuestions);
//     dto.setAnswered(answered);
//     dto.setNotAnswered(notAnswered);

//     dto.setMarkedForReview(markedForReview);
//     dto.setAnsweredAndMarkedForReview(answeredAndMarkedForReview);

//     dto.setNotVisited(notVisited);
//     dto.setVisitedAndNotAnswered(visitedAndNotAnswered);

//     dto.setMaxPossibleMarks(maxPossibleMarks);

//     dto.setTotalTimeSpentSeconds(seconds);
//     dto.setTotalTimeSpent(formatDuration(seconds));

//     dto.setMessage("Exam summary generated with time calculation");

//     return dto;
// }



// @Override
// @Transactional
// public Map<String, Object> confirmSubmission( Long examId,String subdomin) {

//     /* ===================== AUTH ===================== */
//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//     if (auth == null || !auth.isAuthenticated()) {
//         throw new RuntimeException("Unauthorized");
//     }
    
//     User user = (User) auth.getPrincipal();
//     Long userId = user.getUserId();   // 🔥 ADD THIS
    

//     // User user = (User) auth.getPrincipal();
//     if (!user.getUserId().equals(userId)) {
//         throw new RuntimeException("User ID mismatch");
//     }

//     /* ===================== EXAM ===================== */
//     Exam exam = examRepository.findById(examId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     if (!exam.getUser().getUserId().equals(userId)) {
//         throw new RuntimeException("Exam does not belong to user");
//     }

//     if (exam.getStatus() == Exam.ExamStatus.COMPLETED) {
//         throw new RuntimeException("Exam already submitted");
//     }

//     Long tenantId = exam.getTenantId();

//     /* ===================== READ REDIS ===================== */
//     List<ExamResult> redisResults =
//             redisExamResultService.getNormalExamResultsFromRedis(
//                     tenantId,
//                     userId,
//                     examId
//             );

//     if (redisResults == null || redisResults.isEmpty()) {
//         throw new RuntimeException("No exam results found in Redis");
//     }

//     /* ===================== TENANT ===================== */
//     Tenant tenant = tenantRepository.findById(tenantId)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     boolean usePublic = "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

//     /* ===================== MARKS + TIME ===================== */
//     int totalMarks = 0;
//     Duration totalTime = Duration.ZERO;

//     for (ExamResult r : redisResults) {

//         String correctAnswer;

//         if (usePublic) {
//             correctAnswer = questionPublicRepository
//                     .findByQid(r.getQid())
//                     .orElseThrow()
//                     .getCorrectAnswerOption();
//         } else {
//             correctAnswer = questionTenantRepository
//                     .findByQid(r.getQid())
//                     .orElseThrow()
//                     .getCorrectAnswerOption();
//         }

//         r.setCorrectAnswerOption(correctAnswer);

//         if (r.getAnswerOption() == null) {
//             r.setMarks(0);
//             r.setValidateAnswer(null);
//         }
//         else if (r.getAnswerOption().equals(correctAnswer)) {
//             r.setMarks(4);
//             r.setValidateAnswer("CORRECT");
//             totalMarks += 4;
//         }
//         else {
//             r.setMarks(-1);
//             r.setValidateAnswer("WRONG");
//             totalMarks -= 1;
//         }

//         if (r.getDuration() != null) {
//             totalTime = totalTime.plus(r.getDuration());
//         }

//         r.setExamId(examId);
//         r.setUserId(userId);
//         r.setTenantId(tenantId);
//     }

//     /* ===================== SAVE EXAM_RESULTS ===================== */
//     examResultRepository.saveAll(redisResults);

//     /* ===================== FINAL RESULT ===================== */
//     ExamFinalResult finalResult = new ExamFinalResult();
//     finalResult.setExamId(examId);
//     finalResult.setTenantId(tenantId);
//     finalResult.setTotalMarks(totalMarks);
//     finalResult.setTotalTimeSpent(formatDuration(totalTime));
//     finalResult.setSubmittedDateTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));

//     examFinalResultRepository.save(finalResult);

//     /* ===================== CHAPTER AGGREGATION ===================== */
//     Map<String, List<ExamResult>> byChapter =
//             redisResults.stream()
//                     .collect(Collectors.groupingBy(ExamResult::getChapter));

//     List<ChapterResult> chapterResults = new ArrayList<>();

//     for (Map.Entry<String, List<ExamResult>> entry : byChapter.entrySet()) {

//         String chapterName = entry.getKey();
//         List<ExamResult> list = entry.getValue();

//         int chapterMarks = list.stream().mapToInt(ExamResult::getMarks).sum();
//         int chapterTotalMarks = list.size() * 4;

//         Duration chapterTime = list.stream()
//                 .map(r -> r.getDuration() != null ? r.getDuration() : Duration.ZERO)
//                 .reduce(Duration.ZERO, Duration::plus);

//         double percentage =
//                 chapterTotalMarks > 0
//                         ? Math.max(0, (double) chapterMarks / chapterTotalMarks * 100)
//                         : 0;

//         String qid = list.get(0).getQid();
//         String subject;
//         String chapterId;

//         if (usePublic) {
//             QuestionPublic q = questionPublicRepository.findByQid(qid).orElseThrow();
//             subject = q.getSubject().name();
//             chapterId = q.getChapterId();
//         } else {
//             QuestionTenant q = questionTenantRepository.findByQid(qid).orElseThrow();
//             subject = q.getSubject().name();
//             chapterId = q.getChapterId();
//         }

//         ChapterResult cr = new ChapterResult();
//         cr.setExamId(examId);
//         cr.setUserId(userId);
//         cr.setTenantId(tenantId);
//         cr.setExamFinalResult(finalResult);
//         cr.setExamType(exam.getExamType());

//         cr.setChapter(new ChapterResult.Chapter(
//                 chapterId,
//                 chapterName,
//                 subject
//         ));

//         cr.setTimeSpent(formatDuration(chapterTime));
//         cr.setMarks(chapterMarks);
//         cr.setTotalMarks(chapterTotalMarks);
//         cr.setPercentage(percentage);
//         cr.setAiAnalysis(getAiAnalysis(percentage));

//         chapterResults.add(cr);
//     }

//     chapterResultRepository.saveAll(chapterResults);

//     /* ===================== UPDATE EXAM ===================== */
//     exam.setStatus(Exam.ExamStatus.COMPLETED);
//     exam.setUpdatedAt(LocalDateTime.now());
//     examRepository.save(exam);

//     /* ===================== CLEAR REDIS ===================== */
//     redisExamResultService.deleteNormalExamResultsFromRedis(
//             tenantId,
//             userId,
//             examId
//     );

//     /* ===================== RESPONSE ===================== */
//     Map<String, Object> response = new LinkedHashMap<>();
//     response.put("message", "Exam submission confirmed and results saved successfully");
//     response.put("examId", examId);
//     response.put("totalMarks", totalMarks);
//     response.put("resultsSavedCount", redisResults.size());
//     response.put("submittedDateTime", finalResult.getSubmittedDateTime());

//     // 🔥 ADD THIS
// response.put(
//     "chapters",
//     chapterResults.stream()
//         .map(this::convertChapterResultToMap)
//         .collect(Collectors.toList())
// );

//     return response;
// }

// private Map<String, Object> convertChapterResultToMap(ChapterResult cr) {
//     Map<String, Object> map = new LinkedHashMap<>();

//     map.put("chapter", Map.of(
//             "id", cr.getChapter().getId(),
//             "name", cr.getChapter().getName(),
//             "subject", cr.getChapter().getSubject()
//     ));

//     map.put("timeSpent", cr.getTimeSpent());
//     map.put("percentage", cr.getPercentage());
//     map.put("examType", cr.getExamType());
//     map.put("marks", cr.getMarks());
//     map.put("aiAnalysis", cr.getAiAnalysis());
//     map.put("totalMarks", cr.getTotalMarks());

//     return map;
// }

// private String formatDuration(Duration duration) {
//     if (duration == null) return "00:00:00";

//     return String.format(
//             "%02d:%02d:%02d",
//             duration.toHours(),
//             duration.toMinutesPart(),
//             duration.toSecondsPart()
//     );
// }



// private String getAiAnalysis(double percentage) {
//     if (percentage >= 75) {
//         return "Excellent performance! Keep up the good work.";
//     } else if (percentage >= 50) {
//         return "Good effort. Focus on improving weak areas.";
//     } else {
//         return "Need Improvement. Review this chapter's concepts and practice more.";
//     }
// }


// @Override
// @Transactional(readOnly = true)
// public Map<String, Object> getFinalResults(Long examId, String subdomain) {

//     /* ================= AUTH ================= */

//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//     if (auth == null || !auth.isAuthenticated()) {
//         throw new RuntimeException("Unauthorized");
//     }

//     User user = (User) auth.getPrincipal();

//     /* ================= TENANT ================= */

//     Tenant tenant = tenantRepository.findBySubdomain(subdomain)
//             .orElseThrow(() -> new RuntimeException("Tenant not found"));

//     /* ================= EXAM ================= */

//     Exam exam = examRepository.findById(examId)
//             .orElseThrow(() -> new RuntimeException("Exam not found"));

//     if (!exam.getUser().getUserId().equals(user.getUserId())) {
//         throw new RuntimeException("Exam does not belong to user");
//     }

//     if (exam.getStatus() != Exam.ExamStatus.COMPLETED &&
//         exam.getStatus() != Exam.ExamStatus.ABORTED) {
//         throw new RuntimeException("Exam not completed or aborted");
//     }

//     /* ================= FINAL RESULT ================= */

//     ExamFinalResult finalResult =
//             examFinalResultRepository
//                     .findByExamId(examId)
//                     .orElseThrow(() ->
//                             new RuntimeException("Final result not found"));

//     /* ================= CHAPTER RESULTS ================= */

//     List<ChapterResult> chapters =
//             chapterResultRepository.findByExamId(examId);

//     if (chapters.isEmpty()) {
//         throw new RuntimeException("No chapter results found");
//     }

//     /* ================= TOTAL PERCENTAGE ================= */

//     int totalMarks = finalResult.getTotalMarks();

//     int maxMarks = chapters.stream()
//             .mapToInt(ChapterResult::getTotalMarks)
//             .sum();

//     double totalPercentage =
//             maxMarks > 0 ? (double) totalMarks / maxMarks * 100 : 0;

//     /* ================= RESPONSE ================= */

//     Map<String, Object> response = new LinkedHashMap<>();

//     response.put("totalTimeSpent", finalResult.getTotalTimeSpent());

//     response.put(
//             "chapters",
//             chapters.stream()
//                     .map(this::convertChapterResultToMap)
//                     .toList()
//     );

//     response.put("submittedDateTime", finalResult.getSubmittedDateTime());
//     response.put("examType", exam.getExamType());
//     response.put("totalMarks", totalMarks);
//     response.put("totalPercentage", Math.max(0, totalPercentage));

//     /* ================= SWOT ================= */

//     response.put("swot", buildSwotFromChapterResults(chapters));

//     return response;
// }



// private Map<String, Object> buildSwotFromChapterResults(
//     List<ChapterResult> chapters
// ) {
   
//     Map<String, List<ChapterResult>> bySubject =
//     chapters.stream()
//             .collect(Collectors.groupingBy(
//                     (ChapterResult c) -> String.valueOf(
//                             c.getChapter().getSubject()
//                     ).toUpperCase()
//             ));


// Map<String, Object> swot = new LinkedHashMap<>();

// for (String subject : bySubject.keySet()) {
//     List<ChapterResult> list = bySubject.get(subject);

//     Map<String, Object> subjectSwot = new LinkedHashMap<>();
//     subjectSwot.put("strengths", buildStrengthMessages(list, subject));
//     subjectSwot.put("weaknesses", buildWeaknessMessages(list, subject));
//     subjectSwot.put("opportunities", buildOpportunityMessages(list, subject));
//     subjectSwot.put("threats", buildThreatMessages(list, subject));

//     swot.put(capitalize(subject), subjectSwot);
// }

// return swot;
// }

// private Map<String, Object> buildStrengthMessages(
//     List<ChapterResult> chapters,
//     String subject
// ) {
// Map<String, Object> result = buildStrengths(
//         Map.of(subject, chapters),
//         Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
//         Map.of(subject, chapters.stream()
//                 .map(c -> parseDuration(c.getTimeSpent()))
//                 .reduce(Duration.ZERO, Duration::plus)),
//         true
// );

// Object value = result.get(capitalize(subject));
// return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
// }


// private Map<String, Object> buildWeaknessMessages(
//     List<ChapterResult> chapters,
//     String subject
// ) {
// Map<String, Object> result = buildWeaknesses(
//         Map.of(subject, chapters),
//         Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
//         Map.of(subject, (int) chapters.stream().filter(c -> c.getMarks() == 0).count()),
//         Map.of(subject, new HashMap<>())
// );

// Object value = result.get(capitalize(subject));
// return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
// }



// private Map<String, Object> buildOpportunityMessages(
//     List<ChapterResult> chapters,
//     String subject
// ) {
// Map<String, Object> result = buildOpportunities(
//         Map.of(subject, chapters),
//         Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
//         Map.of(subject, "BASIC")
// );

// Object value = result.get(capitalize(subject));
// return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
// }

// private Map<String, Object> buildThreatMessages(
//     List<ChapterResult> chapters,
//     String subject
// ) {
// Map<String, Object> result = buildThreats(
//         Map.of(subject, chapters),
//         Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
//         Map.of(subject, chapters.stream()
//                 .map(c -> parseDuration(c.getTimeSpent()))
//                 .reduce(Duration.ZERO, Duration::plus)),
//         Map.of(subject, new HashMap<>())
// );

// Object value = result.get(capitalize(subject));
// return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
// }



// // --- STRENGTHS ---
// private Map<String, Object> buildStrengths(
//     Map<String, List<ChapterResult>> subjectChapters,
//     Map<String, Integer> subjectScores,
//     Map<String, Duration> subjectTimes,
//     boolean isNegativeMarking
// ) {
//     Map<String, Object> strengths = new LinkedHashMap<>();
//     for (String subject : subjectChapters.keySet()) {
//         List<ChapterResult> chapters = subjectChapters.get(subject);
//         int totalScore = subjectScores.getOrDefault(subject, 0);
//         Duration timeSpent = subjectTimes.getOrDefault(subject, Duration.ZERO);
//         double percentage = (double) totalScore / 180 * 100;
//         List<ChapterResult> sortedChapters = chapters.stream()
//             .sorted(Comparator.comparingDouble(ChapterResult::getPercentage).reversed())
//             .toList();
//         Map<String, Object> subjectStrength = new LinkedHashMap<>();
//         List<String> messages = new ArrayList<>();

//         // 1. Excellent Timing + High Accuracy
//         boolean goodTiming = (
//             (subject.equalsIgnoreCase("BOTANY") && timeSpent.toMinutes() <= 25) ||
//             (subject.equalsIgnoreCase("ZOOLOGY") && timeSpent.toMinutes() <= 25) ||
//             (subject.equalsIgnoreCase("CHEMISTRY") && timeSpent.toMinutes() <= 40) ||
//             (subject.equalsIgnoreCase("PHYSICS") && timeSpent.toMinutes() <= 45)
//         );
//         if (percentage > 80 && goodTiming) {
//             messages.add("Excellent timing—completed in a short duration with highly accurate answers!");
//             if (percentage > 99) {
//                 messages.add("You've rocked both the score and the timing!");
//             }
//         }
//         // 2. >75%
//         if (percentage > 75) {
//             messages.add("Great job! You've performed well in these chapters:");
//             List<String> top2 = sortedChapters.stream().limit(2).map(ch ->ch.getChapter().getName()).toList();
//             subjectStrength.put("topChapters", top2);
//             if (percentage > 90) {
//                 messages.add("Impressive! You've made barely any mistakes.");
//             }
//             if (percentage > 99) {
//                 messages.add("Outstanding! You've absolutely rocked every chapter.");
//             }
//         }
//         // 3. Negative Marking Disabled AND Score = 100%
//         if (!isNegativeMarking && percentage == 100) {
//             messages.add("Flawless! You didn't make a single mistake.");
//         }
//         // 4. >50%
//         if (percentage > 50 && percentage <= 75) {
//             messages.add("Good job! You've performed well in these chapters:");
//             List<String> top3 = sortedChapters.stream().limit(3).map(ch -> ch.getChapter().getName()).toList();
//             subjectStrength.put("topChapters", top3);
//         }
//         // 5. <50% but any chapter >70%
//         if (percentage < 50) {
//             List<String> highChapters = sortedChapters.stream()
//                 .filter(ch -> ch.getPercentage() > 70)
//                 .limit(3)
//                 .map(ch ->ch.getChapter().getName())
//                 .toList();
//             if (!highChapters.isEmpty()) {
//                 messages.add("Nice work! Despite the overall score, you've done well in these chapters:");
//                 subjectStrength.put("topChapters", highChapters);
//             }
//         }
//         if (!messages.isEmpty()) {
//             subjectStrength.put("subject", capitalize(subject));
//             subjectStrength.put("messages", messages);
//             strengths.put(capitalize(subject), subjectStrength);
//         }
//     }
//     return strengths;
// }

// // --- WEAKNESSES ---
// private Map<String, Object> buildWeaknesses(
//     Map<String, List<ChapterResult>> subjectChapters,
//     Map<String, Integer> subjectScores,
//     Map<String, Integer> subjectUnansweredCounts,
//     Map<String, Map<String, Integer>> chapterUnansweredCounts
// ) {
//     Map<String, Object> weaknesses = new LinkedHashMap<>();
//     for (String subject : subjectChapters.keySet()) {
//         List<ChapterResult> chapters = subjectChapters.get(subject);
//         int totalScore = subjectScores.getOrDefault(subject, 0);
//         double percentage = (double) totalScore / 180 * 100;
//         Map<String, Object> subjectWeakness = new LinkedHashMap<>();
//         List<String> messages = new ArrayList<>();
//         List<String> lowestChapters = chapters.stream()
//             .sorted(Comparator.comparingDouble(ChapterResult::getPercentage))
//             .limit(3)
//             .map(ch -> ch.getChapter().getName())
//             .toList();


//         // 1. <50% overall
//         if (percentage < 50) {
//             messages.add("Needs more focused attention in these chapters:");
//             subjectWeakness.put("lowestChapters", lowestChapters);
//             messages.add("Now's the time to rebuild confidence:\n📌 Prioritize targeted revision and take chapter-level practice tests to strengthen your fundamentals.");
//         }
//         // 2. >20% unanswered
//         int unanswered = subjectUnansweredCounts.getOrDefault(subject, 0);
//         int totalQuestions = chapters.stream().mapToInt(ChapterResult::getTotalMarks).sum() / 4;
//         double unansweredRatio = totalQuestions > 0 ? (double) unanswered / totalQuestions : 0;
//         if (unansweredRatio > 0.2) {
//             messages.add("More than 20% of questions were left unanswered in this subject. This suggests hesitation, time management issues, or concept uncertainty.");
//             messages.add("Recommendations:\n• Reattempt the full mock under timed conditions\n• Focus on strengthening weaker topics\n• Use Mark & Review strategies during timed practice");
//         }
//         // 3. Chapters >30% unanswered
//         Map<String, Integer> chapterUnans = chapterUnansweredCounts.getOrDefault(subject, new HashMap<>());
//         List<String> uncertainChapters = new ArrayList<>();
//         for (Map.Entry<String, Integer> entry : chapterUnans.entrySet()) {
//             int chapterTotal = chapters.stream().filter(ch ->ch.getChapter().getName()
//             .equals(entry.getKey()))
//                 .mapToInt(ChapterResult::getTotalMarks).sum() / 4;
//             double chapterUnansRatio = chapterTotal > 0 ? (double) entry.getValue() / chapterTotal : 0;
//             if (chapterUnansRatio > 0.3) {
//                 uncertainChapters.add(entry.getKey());
//             }
//         }
//         if (!uncertainChapters.isEmpty()) {
//             messages.add("The following chapters had >30% unanswered questions, indicating lack of familiarity or uncertainty:");
//             subjectWeakness.put("uncertainChapters", uncertainChapters);
//             messages.add("Suggestions for Improvement:\n• Review each chapter's concept\n• Attempt chapter-level topic tests with answer review mode");
//         }
//         if (!messages.isEmpty()) {
//             subjectWeakness.put("subject", capitalize(subject));
//             subjectWeakness.put("messages", messages);
//             weaknesses.put(capitalize(subject), subjectWeakness);
//         }
//     }
//     return weaknesses;
// }

// // --- OPPORTUNITIES ---
// private Map<String, Object> buildOpportunities(
//     Map<String, List<ChapterResult>> subjectChapters,
//     Map<String, Integer> subjectScores,
//     Map<String, String> subjectDifficulty
// ) {
//     Map<String, Object> opportunities = new LinkedHashMap<>();
//     for (String subject : subjectChapters.keySet()) {
//         List<ChapterResult> chapters = subjectChapters.get(subject);
//         int totalScore = subjectScores.getOrDefault(subject, 0);
//         double percentage = (double) totalScore / 180 * 100;
//         Map<String, Object> subjectOpp = new LinkedHashMap<>();
//         List<String> messages = new ArrayList<>();
//         List<String> lowestChapters = chapters.stream()
//             .sorted(Comparator.comparingDouble(ChapterResult::getPercentage))
//             .limit(2)
//             .map(ch ->ch.getChapter().getName()).toList();
            

//         messages.add("📌 Stay Focused. Stay Ready. NEET SWAN isn't just a platform—it's your launchpad to success.");
//         messages.add("✅ Take frequent mock tests\n✅ Review what clicks and what needs attention\n✅ Practice not until you get it right, but until you can't get it wrong.");
//         if (percentage > 60) {
//             messages.add("Solid effort—you're on your way to mastery!");
//         }
//         messages.add("📌 Keep pushing—you're stronger than you think. Do focus on below.");
//         messages.add("🟡 Basic difficulty selected. Keep going! Practice more Intermediate and Advanced difficulty-level mock tests to strengthen your preparation.");
//         subjectOpp.put("lowestChapters", lowestChapters);

//         subjectOpp.put("subject", capitalize(subject));
//         subjectOpp.put("messages", messages);
//         opportunities.put(capitalize(subject), subjectOpp);
//     }
//     return opportunities;
// }

// // --- THREATS ---
// private Map<String, Object> buildThreats(
//     Map<String, List<ChapterResult>> subjectChapters,
//     Map<String, Integer> subjectScores,
//     Map<String, Duration> subjectTimes,
//     Map<String, Map<String, Integer>> chapterWrongAnswers
// ) {
//     Map<String, Object> threats = new LinkedHashMap<>();
//     for (String subject : subjectChapters.keySet()) {
//         List<ChapterResult> chapters = subjectChapters.get(subject);
//         int totalScore = subjectScores.getOrDefault(subject, 0);
//         Duration timeSpent = subjectTimes.getOrDefault(subject, Duration.ZERO);
//         Map<String, Object> subjectThreat = new LinkedHashMap<>();
//         List<String> messages = new ArrayList<>();
//         List<String> highestNegChapters = chapters.stream()
//         .sorted(Comparator.comparingInt(ChapterResult::getMarks))
//         .limit(3)
//         .map(ch -> ch.getChapter().getName())
//         .toList();
//     List<String> lowestChapters = chapters.stream()
//         .sorted(Comparator.comparingDouble(ChapterResult::getPercentage))
//         .limit(2)
//         .map(ch -> ch.getChapter().getName())
//         .toList();

//         // Chemistry threats
//         if (subject.equalsIgnoreCase("CHEMISTRY")) {
//             messages.add("🚫 Top Chapters with Highest Negative Marks:");
//             messages.addAll(highestNegChapters);
//             messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
//             messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
//             messages.addAll(lowestChapters);
//             messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
//             if (timeSpent.toMinutes() > 65) {
//                 messages.add("🧪 Chemistry (Time > 65 mins)\nWork on your timing in Chemistry. Try timed chapter NEET SWAN tests and quick-recall drills to boost speed.");
//             }
//         }
//         // Physics threats
//         else if (subject.equalsIgnoreCase("PHYSICS")) {
//             messages.add("🚫 Top Chapters with Highest Negative Marks:");
//             messages.addAll(highestNegChapters);
//             messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
//             messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
//             messages.addAll(lowestChapters);
//             messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
//             if (timeSpent.toMinutes() > 70) {
//                 messages.add("🎯 Physics (Time > 70 mins)\nPhysics took longer than expected. Practice more NEETSWAN under exam-like conditions to improve problem-solving speed.");
//             }
//         }
//         // Botany threats
//         else if (subject.equalsIgnoreCase("BOTANY")) {
//             messages.add("🚫 Top Chapters with Highest Negative Marks:");
//             messages.addAll(highestNegChapters);
//             messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
//             messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
//             messages.addAll(lowestChapters);
//             messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
//             if (timeSpent.toMinutes() > 25) {
//                 messages.add("🌿 Botany (Time > 25 mins)\nBotany took longer than expected. Focus on improving your reading speed and concept recall for plant biology topics.");
//             }
//         }
//         // Zoology threats
//         else if (subject.equalsIgnoreCase("ZOOLOGY")) {
//             messages.add("🚫 Top Chapters with Highest Negative Marks:");
//             messages.addAll(highestNegChapters);
//             messages.add("Review these chapters carefully and attempt targeted practice to reduce avoidable errors. Mastery is often about minimizing slips just as much as scoring high!");
//             messages.add("📉 Lowest Scoring Chapters – Needs Immediate Attention:");
//             messages.addAll(lowestChapters);
//             messages.add("Attempt chapter-wise –specific practice tests –build confidence at a time.");
//             if (timeSpent.toMinutes() > 25) {
//                 messages.add("🦁 Zoology (Time > 25 mins)\nZoology took longer than expected. Practice more animal biology concepts and improve your diagram interpretation skills.");
//             }
//         }
        
//         if (!messages.isEmpty()) {
//             subjectThreat.put("subject", capitalize(subject));
//             subjectThreat.put("messages", messages);
//             threats.put(capitalize(subject), subjectThreat);
//         }
//     }
//     return threats;
// }

// // --- Helper ---
// private String capitalize(String s) {
//     if (s == null || s.isEmpty()) return s;
//     return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
// }

// private Duration parseDuration(String time) {
//     if (time == null || time.isBlank()) return Duration.ZERO;
//     String[] p = time.split(":");
//     return Duration.ofHours(Long.parseLong(p[0]))
//             .plusMinutes(Long.parseLong(p[1]))
//             .plusSeconds(Long.parseLong(p[2]));
// }


// } 
package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Tenant;
import com.brihathi.Multi_Tenant.entity.Batch;
import com.brihathi.Multi_Tenant.entity.Branch;
import com.brihathi.Multi_Tenant.entity.ExamResult;
import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.entity.QuestionTenant;
import com.brihathi.Multi_Tenant.entity.Chapter;
import com.brihathi.Multi_Tenant.entity.ExamFinalResult;
import com.brihathi.Multi_Tenant.entity.ChapterResult;
import com.brihathi.Multi_Tenant.repository.ExamRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.TenantRepository;
import com.brihathi.Multi_Tenant.repository.QuestionPublicRepository;
import com.brihathi.Multi_Tenant.repository.QuestionTenantRepository;
import com.brihathi.Multi_Tenant.repository.BatchRepository;
import com.brihathi.Multi_Tenant.repository.BranchRepository;
import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
import com.brihathi.Multi_Tenant.repository.ChapterRepository;
import com.brihathi.Multi_Tenant.repository.ExamFinalResultRepository;
import com.brihathi.Multi_Tenant.repository.ChapterResultRepository;
import com.brihathi.Multi_Tenant.repository.DifficultyRepository;
import com.brihathi.Multi_Tenant.repository.GradeRepository;
import com.brihathi.Multi_Tenant.service.ExamService;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
import com.brihathi.Multi_Tenant.service.RedisTestService;
// import com.brihathi.Multi_Tenant.producer.MessageProducer;
import com.brihathi.Multi_Tenant.dto.ExamResultMessageDTO;
import com.brihathi.Multi_Tenant.dto.NormalExamResponse;
import com.brihathi.Multi_Tenant.dto.NormalExamDTO;
import com.brihathi.Multi_Tenant.dto.CreateNormalExamRequest;
import com.brihathi.Multi_Tenant.dto.QuestionPreviewDTO;
import com.brihathi.Multi_Tenant.dto.StartExamResponseDTO;
import com.brihathi.Multi_Tenant.dto.UpdateQuestionRequestDTO;
import com.brihathi.Multi_Tenant.dto.QuestionStatusDTO;
import com.brihathi.Multi_Tenant.dto.EndExamSummaryDTO;
import com.brihathi.Multi_Tenant.dto.ConfirmChapterAnalysisDTO;
import com.brihathi.Multi_Tenant.dto.ConfirmChapterInfoDTO;
import com.brihathi.Multi_Tenant.dto.ConfirmSubmissionResponseDTO;
import com.brihathi.Multi_Tenant.dto.MessageDTO;
// import com.brihathi.Multi_Tenant.dto.MessageDTO;
import com.brihathi.Multi_Tenant.dto.ExamFinalResultDTO;
import com.brihathi.Multi_Tenant.dto.TenantDTO;
import com.brihathi.Multi_Tenant.dto.AbortExamResponseDTO;
import com.brihathi.Multi_Tenant.dto.BatchDTO;
import com.brihathi.Multi_Tenant.dto.BranchDTO;
import com.brihathi.Multi_Tenant.dto.ChapterResultDTO;
import com.brihathi.Multi_Tenant.dto.UserDTO;
import com.brihathi.Multi_Tenant.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
 
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.time.ZoneId;

 
@Service
public class ExamServiceImpl implements ExamService {
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceImpl.class);
 
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final QuestionPublicRepository questionPublicRepository;
    private final TenantRepository tenantRepository;
    private final QuestionTenantRepository questionTenantRepository;
    private final ExamResultRepository examResultRepository;
    private final RedisTestService redisExamResultService;
    private final ChapterRepository chapterRepository;
    private final BranchRepository branchRepository;
    private final BatchRepository batchRepository;
    private final ExamFinalResultRepository examFinalResultRepository;
    private final ChapterResultRepository chapterResultRepository;
    private final MessageProducer messageProducer;
    private final DifficultyRepository difficultyRepository;
    private final GradeRepository gradeRepository;
 
    @Autowired
    public ExamServiceImpl(ExamRepository examRepository,
                          UserRepository userRepository,
                          BatchRepository batchRepository,
                          BranchRepository branchRepository,
                        //   QuestionRepository questionRepository,
                        TenantRepository tenantRepository,
                        QuestionPublicRepository questionPublicRepository,
                        QuestionTenantRepository questionTenantRepository,
                          ExamResultRepository examResultRepository,
                          ChapterResultRepository chapterResultRepository,
                          RedisTestService redisExamResultService,
                        //   RedisTestService redisTestService,
                          ChapterRepository chapterRepository,
                          ExamFinalResultRepository examFinalResultRepository,
                          MessageProducer messageProducer,
                          DifficultyRepository difficultyRepository,
                          GradeRepository gradeRepository) {
        this.examRepository = examRepository;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.batchRepository = batchRepository;
        // this.questionRepository = questionRepository;
        this.tenantRepository = tenantRepository;
        this.questionPublicRepository = questionPublicRepository;
        this.questionTenantRepository = questionTenantRepository;
        this.examResultRepository = examResultRepository;
        this.chapterResultRepository = chapterResultRepository;
        this.redisExamResultService = redisExamResultService;
        // this.redisTestService = redisTestService;
        this.chapterRepository = chapterRepository;
        this.examFinalResultRepository = examFinalResultRepository;
        this.messageProducer = messageProducer;
        this.difficultyRepository = difficultyRepository;
        this.gradeRepository = gradeRepository;
    }
 

    @Override
    @Transactional
    public Exam createExam(Exam exam) {
        try {
            logger.info("Creating exam with data: {}", exam);
           
            // Validate user exists
            User user = userRepository.findById(exam.getUser().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            exam.setUser(user);
            logger.debug("Found user: {}", user.getUserId());
 
            // Set subject ID based on exam type
            if (exam.getExamType() == Subject.ALL) {
                exam.setSubjectId("ALL");
            } else {
                exam.setSubjectId(exam.getExamType().name().substring(0, 3));
            }
            logger.debug("Set subject ID: {}", exam.getSubjectId());
 
            // Set chapter ID based on exam type and subject
            if (exam.getExamType() == Subject.ALL) {
                exam.setChapterId("ALL-ALL"); // ALL_SUBJECTS
            } else if (exam.getChapterId() == null || exam.getChapterId().isEmpty() || exam.getChapterId().equals("ALL")) {
                // Only set to ALL if no specific chapter is provided or if "ALL" is selected
                switch (exam.getExamType()) {
                    case PHYSICS -> exam.setChapterId("PHY-ALL"); // ALL_PHYSICS
                    case CHEMISTRY -> exam.setChapterId("CHE-ALL"); // ALL_CHEMISTRY
                    case BOTANY -> exam.setChapterId("BOT-ALL"); // ALL_BOTANY
                    case ZOOLOGY -> exam.setChapterId("ZOO-ALL"); // ALL_ZOOLOGY
                    default -> exam.setChapterId(exam.getChapterId()); // Specific chapter
                }
            } else {
                // If a chapter ID is already set, use it directly
                // We assume that createAndStartExam or another process has already validated/converted it
                logger.debug("Using provided chapter ID: {}", exam.getChapterId());
            }
            logger.debug("Set chapter ID: {}", exam.getChapterId());
 
            // Set total marks and duration based on exam type
            if (exam.getExamType() == Subject.ALL) {
                exam.setTotalMarks(720); // 180 marks per subject * 4 subjects
                exam.setTotalDuration(Duration.ofMinutes(200));
            } else {
                // Check if it's chapter-wise or full subject exam
                String chapterId = exam.getChapterId();
                boolean isChapterWise = chapterId != null && !chapterId.isEmpty() && 
                                      !chapterId.equals("ALL") && !chapterId.endsWith("-ALL") &&
                                      !chapterId.contains("ALL");
                
                if (isChapterWise) {
                    // Chapter-wise exam: 30 questions * 4 marks = 120 marks, 30 minutes
                    exam.setTotalMarks(120);
                    exam.setTotalDuration(Duration.ofMinutes(30));
                } else {
                    // Full subject exam: 45 questions * 4 marks = 180 marks, 50 minutes
                    exam.setTotalMarks(180);
                    exam.setTotalDuration(Duration.ofMinutes(50));
                }
            }
            logger.debug("Set total marks: {} and duration: {}", exam.getTotalMarks(), exam.getTotalDuration());
 
            // Set initial status
            exam.setStatus(Exam.ExamStatus.PENDING);
 
            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            exam.setCreatedAt(now);
            exam.setUpdatedAt(now);
 
            Exam savedExam = examRepository.save(exam);
            logger.info("Successfully created exam with ID: {}", savedExam.getExamId());
            return savedExam;
 
        } catch (PersistenceException e) {
            logger.error("Database error while creating exam", e);
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                throw new RuntimeException("Database constraint violation: " + cve.getMessage(), e);
            }
            throw new RuntimeException("Error creating exam: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while creating exam", e);
            throw new RuntimeException("Error creating exam: " + e.getMessage(), e);
        }
    }
 
 
@Override
public Map<String, Object> getExamInfo(String subdomain) {

    // 1️⃣ Tenant from subdomain
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() ->
                    new RuntimeException("Tenant not found for subdomain: " + subdomain)
            );

    boolean usePublicTable =
            "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

    // 2️⃣ Subjects logic (UNCHANGED)
    List<Map<String, Object>> subjects = new ArrayList<>();

    List<String> allDifficulties = difficultyRepository.findAll()
            .stream().map(d -> d.getDifficulty()).toList();

    List<String> allGrades = gradeRepository.findAll()
            .stream().map(g -> g.getGrade()).toList();

   
 List<Chapter> allChapters = chapterRepository.findAll();
 Map<String, List<String>> subjectToChapters = new HashMap<>();
 for (Chapter chapter : allChapters) {
     subjectToChapters.computeIfAbsent(chapter.getSubject(), k -> new ArrayList<>()).add(chapter.getChapter());
 }

 // Add ALL subjects option first
 Map<String, Object> allSubjects = new HashMap<>();
 allSubjects.put("subject", Subject.ALL);
 allSubjects.put("chapters", allChapters.stream().map(Chapter::getChapter).distinct().toList());
 allSubjects.put("difficulties", allDifficulties);
 allSubjects.put("grades", allGrades);
 subjects.add(allSubjects);

 // Add individual subjects
 for (Subject subject : Subject.values()) {
     if (subject != Subject.ALL) {
         Map<String, Object> subjectInfo = new HashMap<>();
         subjectInfo.put("subject", subject);
         subjectInfo.put("chapters", subjectToChapters.getOrDefault(subject.name(), List.of()));
         subjectInfo.put("difficulties", allDifficulties);
         subjectInfo.put("grades", allGrades);
         subjects.add(subjectInfo);
     }
    }

   // 3️⃣ TENANT → BRANCH → BATCH (ONLY CURRENT TENANT)
List<Map<String, Object>> tenantsResponse = new ArrayList<>();

Map<String, Object> tenantMap = new HashMap<>();
tenantMap.put("tenantId", tenant.getTenantId());
tenantMap.put("tenantName", tenant.getCollegeName()); // or subdomain

List<Map<String, Object>> branchesResponse = new ArrayList<>();

List<Branch> branches =
        branchRepository.findByTenantId(tenant.getTenantId());

for (Branch branch : branches) {

    Map<String, Object> branchMap = new HashMap<>();
    branchMap.put("branchId", branch.getBranchId());
    branchMap.put("branchName", branch.getBranchName());

    List<Map<String, Object>> batchesResponse = new ArrayList<>();

    List<Batch> batches =
            batchRepository.findByBranchId(branch.getBranchId());

    for (Batch batch : batches) {
        Map<String, Object> batchMap = new HashMap<>();
        batchMap.put("batchId", batch.getBatchId());
        batchMap.put("batchName", batch.getBatchName());
        batchesResponse.add(batchMap);
    }

    branchMap.put("batches", batchesResponse);
    branchesResponse.add(branchMap);
}

tenantMap.put("branches", branchesResponse);
tenantsResponse.add(tenantMap);


    // 4️⃣ FINAL RESPONSE
    Map<String, Object> response = new HashMap<>();
    response.put("subjects", subjects);
    response.put("tenants", tenantsResponse);

    return response;
}

// ================= HELPERS =================

private List<String> extractDistinctChapters(List<?> questions) {
    return questions.stream()
            .map(q -> q instanceof QuestionPublic qp ? qp.getChapter()
                    : ((QuestionTenant) q).getChapter())
            .distinct()
            .sorted()
            .toList();
}

private List<String> extractChaptersBySubject(List<?> questions, Subject subject) {
    return questions.stream()
            .filter(q -> q instanceof QuestionPublic qp
                    ? qp.getSubject() == subject
                    : ((QuestionTenant) q).getSubject() == subject)
            .map(q -> q instanceof QuestionPublic qp ? qp.getChapter()
                    : ((QuestionTenant) q).getChapter())
            .distinct()
            .sorted()
            .toList();
}

@Override
@Transactional
public NormalExamResponse createNormalExam(
        CreateNormalExamRequest request,
        String subdomain
) {

    /* ===================== AUTH ===================== */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }
    User user = (User) auth.getPrincipal();

    /* ===================== TENANT ===================== */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));
    Long tenantId = tenant.getTenantId();

   /* ===================== EXAM CONFIG ===================== */

// Subject examType = request.getExamType();   // ALL / PHYSICS / CHEMISTRY / BOTANY / ZOOLOGY
String chapterId = request.getChapterId();  // optional
Subject subject  = request.getSubject();  // same as examType OR optional

/* ===================== CHAPTER ID FROM CHAPTER NAME ===================== */
// If chapterId looks like a chapter name (contains spaces or is descriptive), 
// try to find the actual chapterId from the database
if (chapterId != null 
        && !chapterId.equals("ALL-ALL") 
        && !chapterId.endsWith("-ALL")
        && (chapterId.contains(" ") || !chapterId.contains("-"))) {  // Looks like a name, not an ID
    // Try to find chapter by subject and chapter name
    if (subject != null && subject != Subject.ALL) {
        Optional<Chapter> chapterOpt = chapterRepository.findBySubjectAndChapter(
                subject.name(), 
                chapterId
        );
        if (chapterOpt.isPresent()) {
            chapterId = chapterOpt.get().getChapterId();  // Replace with actual chapterId
        }
    }
}

int questions;
Duration duration;

/*
 MODE DECISION LOGIC:
 1) examType == ALL                  → ALL SUBJECT EXAM
 2) examType != ALL && chapterId == null → SUBJECT-WISE EXAM
 3) examType != ALL && chapterId != null → CHAPTER-WISE EXAM
*/

/* ===================== EXAM MODE RESOLUTION ===================== */





/* ====== 1️⃣ ALL SUBJECT EXAM ====== */
if (subject == Subject.ALL
        && subject == Subject.ALL
        && "ALL-ALL".equals(chapterId)) {

    questions = 180;
    duration = Duration.ofHours(3);

}

/* ====== 2️⃣ SUBJECT-WISE EXAM ====== */
else if (subject != Subject.ALL
        // && subject == subject
        && (subject.name().substring(0, 3) + "-ALL").equals(chapterId)) {

    questions = 45;
    duration = Duration.ofHours(1);

}

/* ====== 3️⃣ CHAPTER-WISE EXAM ====== */
else if (subject != Subject.ALL
        // && examType == subject
        && !chapterId.endsWith("-ALL")) {

    questions = 30;
    duration = Duration.ofMinutes(30);

}

/* ====== ❌ INVALID COMBINATION ====== */
else {
    throw new RuntimeException(
        "Invalid exam configuration: subject=" + subject +
        ", chapterId=" + chapterId
    );
}

    /* ===================== SUBJECT CODE ===================== */
    String subjectCode =
            subject.equals("ALL") ? "ALL" : subject.name().substring(0, 3);

    /* ===================== QUESTION GENERATION ===================== */
    boolean usePublic =
            "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

    List<QuestionPreviewDTO> previews = new ArrayList<>();
    // List<?> questionsList;

    // if ("CHAPTER".equals(subject)) {
    //     questionsList = usePublic
    //             ? questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
    //                     subject.name(),
    //                     request.getChapterId(),
    //                     request.getDifficulty().name(),
    //                     request.getGrade(),
    //                     questions
    //             )
    //             : questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
    //                     subject.name(),
    //                     request.getChapterId(),
    //                     request.getDifficulty().name(),
    //                     request.getGrade(),
    //                     questions
    //             );
    // } else {
    //     questionsList = usePublic
    //             ? getQuestionsWithWeightagePublic(subject, request.getDifficulty(), request.getGrade(), questions)
    //             : getQuestionsWithWeightageTenant(subject, request.getDifficulty(), request.getGrade(), questions);
    // }
// /* 🔵 CHAPTER-WISE EXAM (ONLY ONE CHAPTER) */
// if (subject != Subject.ALL
//     // && examType == subject
//     && !chapterId.endsWith("-ALL")) {

// if (usePublic) {
//     questionsList =
//             questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
//                     subject.name(),
//                     chapterId,                 // 🔥 ONLY THIS CHAPTER
//                     request.getDifficulty().name(),
//                     request.getGrade(),
//                     30                          // EXACT
//             );
// } else {
//     questionsList =
//             questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
//                     subject.name(),
//                     chapterId,                 // 🔥 ONLY THIS CHAPTER
//                     request.getDifficulty().name(),
//                     request.getGrade(),
//                     30
//             );
// }
// }

// /* 🟡 SUBJECT-WISE / 🟢 ALL-SUBJECT EXAMS */
// else {

// if (usePublic) {
//     questionsList =
//             getQuestionsWithWeightagePublic(
//                     subject,
//                     request.getDifficulty(),
//                     request.getGrade(),
//                     questions
//             );
// } else {
//     questionsList =
//             getQuestionsWithWeightageTenant(
//                     subject,
//                     request.getDifficulty(),
//                     request.getGrade(),
//                     questions
//             );
// }
// if (questionsList.size() < questions) {
//     throw new RuntimeException(
//         "Not enough questions for chapter " + chapterId +
//         ". Required=" + questions +
//         ", Found=" + questionsList.size()
//     );
// }
// }
 
List<Object> questionsList = new ArrayList<>();
   
/* 🔵 CHAPTER-WISE EXAM (ONLY ONE CHAPTER) */
if (subject != Subject.ALL && !chapterId.endsWith("-ALL")) {

List<?> fetched = usePublic
        ? questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
                subject.name(),
                chapterId,
                request.getDifficulty().name(),
                request.getGrade(),
                30
        )
        : questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
                subject.name(),
                chapterId,
                request.getDifficulty().name(),
                request.getGrade(),
                30
        );

questionsList.addAll((Collection<?>) fetched);
}

/* 🟡 SUBJECT-WISE / 🟢 ALL-SUBJECT EXAMS */
else {

if (subject == Subject.ALL) {

    for (Subject sub : List.of(
            Subject.PHYSICS,
            Subject.CHEMISTRY,
            Subject.BOTANY,
            Subject.ZOOLOGY
    )) {

        List<?> subQuestions = usePublic
                ? getQuestionsWithWeightagePublic(sub, request.getDifficulty(), request.getGrade(), 45)
                : getQuestionsWithWeightageTenant(sub, request.getDifficulty(), request.getGrade(), 45);

        questionsList.addAll((Collection<?>) subQuestions);
    }

} else {

    List<?> fetched = usePublic
            ? getQuestionsWithWeightagePublic(subject, request.getDifficulty(), request.getGrade(), questions)
            : getQuestionsWithWeightageTenant(subject, request.getDifficulty(), request.getGrade(), questions);

    questionsList.addAll((Collection<?>) fetched);
}
}

/* ✅ FINAL SAFETY CHECK */
if (questionsList.size() < questions) {
throw new RuntimeException(
    "Not enough questions. Required=" + questions +
    ", Found=" + questionsList.size()
);
}





    for (Object q : questionsList) {
        previews.add(mapToPreviewDTO(q));
    }

    /* ===================== SAVE EXAM ===================== */
    Exam exam = new Exam();
    exam.setUser(user);
    exam.setTenantId(tenantId);
    exam.setExamType(
            subject.equals("ALL") ? Subject.ALL : subject
    );
    exam.setSubjectId(subjectCode);
    // exam.setChapterId(
    //         "CHAPTER".equals(subject)
    //                 ? chapterId  // Use resolved chapterId (from chapterName if needed)
    //                 : subjectCode + "-ALL"
    // );
    if (subject == Subject.ALL) {
        exam.setChapterId("ALL-ALL");
    } 
    else if (chapterId != null && chapterId.endsWith("-ALL")) {
        // Subject-wise exam
        exam.setChapterId(chapterId);   // e.g., ZOO-ALL
    } 
    else {
        // Chapter-wise exam
        exam.setChapterId(chapterId);   // e.g., ZOO06 ✅
    }
    exam.setDifficulty(request.getDifficulty());
    exam.setGrade(request.getGrade());
    exam.setTotalDuration(duration);
    exam.setStatus(Exam.ExamStatus.PENDING);

    examRepository.save(exam);

    /* ===================== SAVE EXAM RESULTS ===================== */
    List<ExamResult> results = new ArrayList<>();

    for (Object q : questionsList) {
    
        String qid;
        String chapter;
        String subjectName;
        String correctAnswer;
    
        if (q instanceof QuestionPublic qp) {
    
            qid = qp.getQid();
            chapter = qp.getChapter();
            subjectName = qp.getSubject().name();
            correctAnswer = qp.getCorrectAnswerOption(); // ✅ IMPORTANT
    
        } else if (q instanceof QuestionTenant qt) {
    
            qid = qt.getQid();
            chapter = qt.getChapter();
            subjectName = qt.getSubject().name();
            correctAnswer = qt.getCorrectAnswerOption(); // ✅ IMPORTANT
    
        } else {
            throw new RuntimeException("Unknown question type");
        }
    
        ExamResult r = new ExamResult();
        r.setExamId(exam.getExamId());
        r.setUserId(user.getUserId());
        r.setTenantId(tenantId);
    
        r.setQid(qid);
        r.setSubject(subjectName);
        r.setChapter(chapter);
    
        r.setAnswered(false);
        r.setVisited(false);
        r.setMarkedForReview(false);
        r.setDuration(Duration.ZERO);
    
        // 🔥 THIS IS THE FIX
        r.setCorrectAnswerOption(correctAnswer);
    
        results.add(r);
    }
    

    examResultRepository.saveAll(results);

    /* ===================== RESPONSE ===================== */
    NormalExamDTO examDTO = new NormalExamDTO(
        exam.getExamId(),                          // Long
        subject.name(),                           // String examType (ALL / PHYSICS / ...)
        subjectCode,                               // String subjectId (PHY / CHE / ...)
        request.getDifficulty(),                   // Difficulty
        exam.getChapterId(),                       // String
        request.getGrade(),                        // String
        questions,                                 // Integer totalMarks ❗ (or totalQuestions if you prefer)
        formatDuration(duration.toSeconds()),      // String totalDuration
        exam.getStatus().name(),                   // String
        null,                                      // startDate
        null,                                      // endDate
        previews                                   // List<QuestionPreviewDTO>
);


    UserDTO userDTO = new UserDTO(
            user.getUserId(),
            user.getName(),
            tenantId,
            user.getUpdatedAt()
    );

    return new NormalExamResponse(examDTO, userDTO);
}

private QuestionPreviewDTO mapToPreviewDTO(Object q) {

    QuestionPreviewDTO dto = new QuestionPreviewDTO();

    if (q instanceof QuestionPublic qp) {

        dto.setQid(qp.getQid());
        dto.setSubject(qp.getSubject().name());
        dto.setChapter(qp.getChapter());
        dto.setChapterId(qp.getChapterId());
        // dto.setQuestionType("MCQ");
        dto.setQuestionText(qp.getQuestionText());

        dto.setAnswerOption1(qp.getAnswerOption1());
        dto.setAnswerOption2(qp.getAnswerOption2());
        dto.setAnswerOption3(qp.getAnswerOption3());
        dto.setAnswerOption4(qp.getAnswerOption4());

    } else if (q instanceof QuestionTenant qt) {

        dto.setQid(qt.getQid());
        dto.setSubject(qt.getSubject().name());
        dto.setChapter(qt.getChapter());
        dto.setChapterId(qt.getChapterId());
        // dto.setQuestionType("MCQ");
        dto.setQuestionText(qt.getQuestionText());

        dto.setAnswerOption1(qt.getAnswerOption1());
        dto.setAnswerOption2(qt.getAnswerOption2());
        dto.setAnswerOption3(qt.getAnswerOption3());
        dto.setAnswerOption4(qt.getAnswerOption4());

    } else {
        throw new RuntimeException("Unknown question type: " + q.getClass());
    }

    return dto;
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

private String formatDuration(long seconds) {
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;

    return String.format("%02d:%02d:%02d", hours, minutes, secs);
}




private List<QuestionPublic> getQuestionsWithWeightagePublicForChapters(
    Subject subject,
    Difficulty difficulty,
    String grade,
    List<String> chapterIds
) {

List<QuestionPublic> result = new ArrayList<>();

for (String chapterId : chapterIds) {

    List<QuestionPublic> questions =
            questionPublicRepository.findRandomQuestionsBySubjectAndChapters(
                    subject.name(),
                    chapterId,
                    difficulty.name(),
                    grade,
                    10 // per chapter (configurable)
            );

    result.addAll(questions);
}

if (result.isEmpty()) {
    throw new RuntimeException("No questions found for selected chapters");
}

Collections.shuffle(result);
return result;
}


private List<QuestionTenant> getQuestionsWithWeightageTenantForChapters(
    Subject subject,
    Difficulty difficulty,
    String grade,
    List<String> chapterIds
) {

List<QuestionTenant> result = new ArrayList<>();

for (String chapterId : chapterIds) {

    List<QuestionTenant> questions =
            questionTenantRepository.findRandomQuestionsBySubjectAndChapters(
                    subject.name(),
                    chapterId,
                    difficulty.name(),
                    grade,
                    10
            );

    result.addAll(questions);
}

if (result.isEmpty()) {
    throw new RuntimeException("No tenant questions found for selected chapters");
}

Collections.shuffle(result);
return result;
}


@Override
@Transactional
public StartExamResponseDTO startNormalExam(Long examId, String subdomain) {

    /* ===================== AUTH ===================== */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    if (!(auth.getPrincipal() instanceof User user)) {
        throw new RuntimeException("Only student can start exam");
    }

    /* ===================== TENANT ===================== */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* ===================== FETCH EXAM ===================== */
    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    /* ===================== VALIDATIONS ===================== */
    if (!exam.getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Unauthorized exam access");
    }

    if (!exam.getTenantId().equals(tenant.getTenantId())) {
        throw new RuntimeException("Tenant mismatch");
    }

    /* ===================== STATUS HANDLING ===================== */

    // ❌ Completed / Aborted → NOT allowed
    if (exam.getStatus() == Exam.ExamStatus.COMPLETED
            || exam.getStatus() == Exam.ExamStatus.ABORTED) {
        throw new RuntimeException("Exam already completed");
    }

    // ✅ Already started → just return snapshot (NO ERROR)
    if (exam.getStatus() == Exam.ExamStatus.IN_PROGRESS) {
        return buildStartExamResponse(exam);
    }

    /* ===================== START EXAM (ONLY PENDING) ===================== */

    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plus(exam.getTotalDuration());

    exam.setStartDate(startTime);
    exam.setEndDate(endTime);
    exam.setStatus(Exam.ExamStatus.IN_PROGRESS);

    examRepository.save(exam);

    /* ===================== REDIS PUSH (NON-CRITICAL) ===================== */
    try {
        List<ExamResult> results =
                examResultRepository.findByExamIdAndUserIdAndTenantId(
                        examId,
                        user.getUserId(),
                        tenant.getTenantId()
                );

        if (!results.isEmpty()) {
            redisExamResultService.saveNormalExamResultsToRedis(
                    tenant.getTenantId(),
                    user.getUserId(),
                    exam.getExamId(),
                    results
            );
        }
    } catch (Exception e) {
        // ❗ Redis must NEVER break exam start
        logger.error("Redis push failed for examId {}", examId, e);
    }

    /* ===================== RESPONSE ===================== */
    return buildStartExamResponse(exam);
}



private StartExamResponseDTO buildStartExamResponse(Exam exam) {

    StartExamResponseDTO dto = new StartExamResponseDTO();
    dto.setExamId(exam.getExamId());
    dto.setSubjectId(exam.getSubjectId());
    dto.setChapterId(exam.getChapterId());
    dto.setExamType(exam.getExamType().name());
    dto.setDifficulty(exam.getDifficulty());
    dto.setGrade(exam.getGrade());
    dto.setTotalMarks(exam.getTotalMarks());
    dto.setTotalDuration(formatDuration(exam.getTotalDuration().toSeconds()));
    dto.setStartDate(exam.getStartDate());
    dto.setEndDate(exam.getEndDate());
    dto.setStatus(exam.getStatus());
    dto.setCreatedAt(exam.getCreatedAt());
    dto.setUpdatedAt(exam.getUpdatedAt());

    return dto;
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
public Map<String, Object> getGeneratedQids(Long examId, String subdomain) {

    /* ===================== TENANT ===================== */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* ===================== AUTH ===================== */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    if (!(auth.getPrincipal() instanceof User user)) {
        throw new RuntimeException("Only user allowed");
    }

    /* ===================== EXAM ===================== */
    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    if (!exam.getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Exam does not belong to user");
    }

    if (!exam.getTenantId().equals(tenant.getTenantId())) {
        throw new RuntimeException("Tenant mismatch");
    }

    /* ===================== FETCH QIDS (DB) ===================== */
    List<String> qids =
            examResultRepository.findQidsByExamIdAndUserIdAndTenantId(
                    examId,
                    user.getUserId(),
                    tenant.getTenantId()
            );

    return Map.of(
            "examId", examId,
            "qids", qids,
            "count", qids.size(),
            "examStatus", exam.getStatus()
    );
}




@Override
@Transactional
public AbortExamResponseDTO abortExam(Long examId, String subdomain) {

    /* ===================== AUTH ===================== */
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }

    if (!(authentication.getPrincipal() instanceof User user)) {
        throw new RuntimeException("Only user can abort exam");
    }

    /* ===================== TENANT ===================== */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* ===================== FETCH EXAM ===================== */
    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    /* ===================== VALIDATIONS ===================== */
    if (!exam.getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Exam does not belong to this user");
    }

    if (!exam.getTenantId().equals(tenant.getTenantId())) {
        throw new RuntimeException("Tenant mismatch");
    }

    if (exam.getStatus() != Exam.ExamStatus.IN_PROGRESS) {
        throw new RuntimeException(
                "Exam cannot be aborted. Current status: " + exam.getStatus()
        );
    }

    /* ===================== ABORT EXAM ===================== */
    exam.setStatus(Exam.ExamStatus.ABORTED);
    Exam savedExam = examRepository.save(exam);

    /* ===================== REDIS CLEANUP (NORMAL EXAM) ===================== */
    try {
        redisExamResultService.deleteNormalExamResultsFromRedis(
                tenant.getTenantId(),
                user.getUserId(),
                examId
        );
    } catch (Exception e) {
        // Redis failure must NOT fail abort
        logger.error("Redis cleanup failed for aborted exam {}", examId, e);
    }

    AbortExamResponseDTO dto = new AbortExamResponseDTO();

    dto.setUserId(user.getUserId());
    dto.setExamId(exam.getExamId());
    dto.setTenantId(exam.getTenantId());
    
    dto.setSubjectId(exam.getSubjectId());
    dto.setChapterId(exam.getChapterId());
    dto.setExamType(exam.getExamType());
    
    dto.setDifficulty(exam.getDifficulty());
    dto.setGrade(exam.getGrade());
    
    dto.setTotalMarks(exam.getTotalMarks());
    dto.setTotalDuration(exam.getTotalDuration().getSeconds());
    
    dto.setStartDate(exam.getStartDate());
    dto.setEndDate(exam.getEndDate());
    
    dto.setStatus(exam.getStatus());
    dto.setCreatedAt(exam.getCreatedAt());
    dto.setUpdatedAt(exam.getUpdatedAt());
    
    return dto;
    
}



@Override
@Transactional
public QuestionStatusDTO updateQuestion(
        Long examId,
        UpdateQuestionRequestDTO request,
        String subdomain
) {

    /* ===================== AUTH ===================== */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth.getPrincipal() instanceof User user)) {
        throw new RuntimeException("Unauthorized");
    }

    /* ===================== TENANT ===================== */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* ===================== EXAM ===================== */
    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    if (!exam.getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Exam does not belong to user");
    }

    if (exam.getStatus() != Exam.ExamStatus.IN_PROGRESS) {
        throw new RuntimeException("Exam not in progress");
    }

    /* ===================== REDIS UPDATE ===================== */
    ExamResult result =
            redisExamResultService.updateNormalExamQuestion(
                    tenant.getTenantId(),
                    user.getUserId(),
                    examId,
                    request
            );

    /* ===================== RESPONSE ===================== */
    QuestionStatusDTO dto = new QuestionStatusDTO();
    dto.setExamId(examId);
    dto.setQid(result.getQid());
    dto.setChapter(result.getChapter());
    dto.setVisited(result.getVisited());
    dto.setAnswered(result.getAnswered());
    dto.setMarkedForReview(result.getMarkedForReview());
    dto.setAnswerOption(result.getAnswerOption());
    dto.setDuration(formatDuration(result.getDuration().toSeconds()));

    return dto;
}


@Override
@Transactional
public EndExamSummaryDTO endNormalExam(Long examId, String subdomain) {

    /* ===================== AUTH ===================== */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth.getPrincipal() instanceof User user)) {
        throw new RuntimeException("Unauthorized");
    }

    /* ===================== TENANT ===================== */
    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* ===================== EXAM ===================== */
    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    if (!exam.getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Exam does not belong to user");
    }

    if (!exam.getTenantId().equals(tenant.getTenantId())) {
        throw new RuntimeException("Tenant mismatch");
    }

    if (exam.getStatus() != Exam.ExamStatus.IN_PROGRESS) {
        throw new RuntimeException("Exam is not in progress");
    }

    /* ===================== FETCH RESULTS ===================== */
    List<ExamResult> results =
            redisExamResultService.getNormalExamResultsFromRedis(
                    tenant.getTenantId(),
                    user.getUserId(),
                    examId
            );

    if (results == null || results.isEmpty()) {
        results = examResultRepository
                .findByExamIdAndUserIdAndTenantId(
                        examId,
                        user.getUserId(),
                        tenant.getTenantId()
                );
    }

    int totalQuestions = results.size();

    int answered = 0;
    int markedForReview = 0;
    int answeredAndMarkedForReview = 0;
    int visited = 0;

    for (ExamResult r : results) {

        if (Boolean.TRUE.equals(r.getVisited())) {
            visited++;
        }

        if (Boolean.TRUE.equals(r.getAnswered())) {
            answered++;
        }

        if (Boolean.TRUE.equals(r.getMarkedForReview())) {
            markedForReview++;
        }

        if (Boolean.TRUE.equals(r.getAnswered())
                && Boolean.TRUE.equals(r.getMarkedForReview())) {
            answeredAndMarkedForReview++;
        }
    }

    int notAnswered = totalQuestions - answered;
    int notVisited = totalQuestions - visited;
    int visitedAndNotAnswered = visited - answered;

    /* ===================== TIME ===================== */
    Duration totalTime =
            redisExamResultService.getNormalExamTotalTime(
                    tenant.getTenantId(),
                    user.getUserId(),
                    examId
            );
    long seconds = totalTime.getSeconds();

    /* ===================== MARKS ===================== */
    int maxPossibleMarks = exam.getTotalMarks();

   
   

    /* ===================== RESPONSE ===================== */
    EndExamSummaryDTO dto = new EndExamSummaryDTO();

    dto.setUserId(user.getUserId());
    dto.setExamId(examId);

    dto.setTotalQuestions(totalQuestions);
    dto.setAnswered(answered);
    dto.setNotAnswered(notAnswered);

    dto.setMarkedForReview(markedForReview);
    dto.setAnsweredAndMarkedForReview(answeredAndMarkedForReview);

    dto.setNotVisited(notVisited);
    dto.setVisitedAndNotAnswered(visitedAndNotAnswered);

    dto.setMaxPossibleMarks(maxPossibleMarks);

    dto.setTotalTimeSpentSeconds(seconds);
    dto.setTotalTimeSpent(formatDuration(seconds));

    dto.setMessage("Exam summary generated with time calculation");

    return dto;
}



@Override
@Transactional
public Map<String, Object> confirmSubmission( Long examId,String subdomin) {

    /* ===================== AUTH ===================== */
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }
    
    User user = (User) auth.getPrincipal();
    Long userId = user.getUserId();   // 🔥 ADD THIS
    

    // User user = (User) auth.getPrincipal();
    if (!user.getUserId().equals(userId)) {
        throw new RuntimeException("User ID mismatch");
    }

    /* ===================== EXAM ===================== */
    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    if (!exam.getUser().getUserId().equals(userId)) {
        throw new RuntimeException("Exam does not belong to user");
    }

    if (exam.getStatus() == Exam.ExamStatus.COMPLETED) {
        throw new RuntimeException("Exam already submitted");
    }

    Long tenantId = exam.getTenantId();

    /* ===================== READ REDIS ===================== */
    List<ExamResult> redisResults =
            redisExamResultService.getNormalExamResultsFromRedis(
                    tenantId,
                    userId,
                    examId
            );

    if (redisResults == null || redisResults.isEmpty()) {
        throw new RuntimeException("No exam results found in Redis");
    }

    /* ===================== TENANT ===================== */
    Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    boolean usePublic = "PUBLIC".equalsIgnoreCase(tenant.getQuestionTable());

    /* ===================== MARKS + TIME ===================== */
    int totalMarks = 0;
    Duration totalTime = Duration.ZERO;

    for (ExamResult r : redisResults) {

        String correctAnswer;

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

        r.setCorrectAnswerOption(correctAnswer);

        if (r.getAnswerOption() == null) {
            r.setMarks(0);
            r.setValidateAnswer(null);
        }
        else if (r.getAnswerOption().equals(correctAnswer)) {
            r.setMarks(4);
            r.setValidateAnswer("CORRECT");
            totalMarks += 4;
        }
        else {
            r.setMarks(-1);
            r.setValidateAnswer("WRONG");
            totalMarks -= 1;
        }

        if (r.getDuration() != null) {
            totalTime = totalTime.plus(r.getDuration());
        }

        r.setExamId(examId);
        r.setUserId(userId);
        r.setTenantId(tenantId);
    }

    /* ===================== SAVE EXAM_RESULTS ===================== */
    examResultRepository.saveAll(redisResults);

    /* ===================== FINAL RESULT ===================== */
    ExamFinalResult finalResult = new ExamFinalResult();
    finalResult.setExamId(examId);
    finalResult.setTenantId(tenantId);
    finalResult.setTotalMarks(totalMarks);
    finalResult.setTotalTimeSpent(formatDuration(totalTime));
    finalResult.setSubmittedDateTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));

    examFinalResultRepository.save(finalResult);

    /* ===================== CHAPTER AGGREGATION ===================== */
    Map<String, List<ExamResult>> byChapter =
            redisResults.stream()
                    .collect(Collectors.groupingBy(ExamResult::getChapter));

    List<ChapterResult> chapterResults = new ArrayList<>();

    for (Map.Entry<String, List<ExamResult>> entry : byChapter.entrySet()) {

        String chapterName = entry.getKey();
        List<ExamResult> list = entry.getValue();

        int chapterMarks = list.stream().mapToInt(ExamResult::getMarks).sum();
        int chapterTotalMarks = list.size() * 4;

        Duration chapterTime = list.stream()
                .map(r -> r.getDuration() != null ? r.getDuration() : Duration.ZERO)
                .reduce(Duration.ZERO, Duration::plus);

        double percentage =
                chapterTotalMarks > 0
                        ? Math.max(0, (double) chapterMarks / chapterTotalMarks * 100)
                        : 0;

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

        ChapterResult cr = new ChapterResult();
        cr.setExamId(examId);
        cr.setUserId(userId);
        cr.setTenantId(tenantId);
        cr.setExamFinalResult(finalResult);
        cr.setExamType(exam.getExamType());

        cr.setChapter(new ChapterResult.Chapter(
                chapterId,
                chapterName,
                subject
        ));

        cr.setTimeSpent(formatDuration(chapterTime));
        cr.setMarks(chapterMarks);
        cr.setTotalMarks(chapterTotalMarks);
        cr.setPercentage(percentage);
        cr.setAiAnalysis(getAiAnalysis(percentage));

        chapterResults.add(cr);
    }

    chapterResultRepository.saveAll(chapterResults);

    /* ===================== UPDATE EXAM ===================== */
    exam.setStatus(Exam.ExamStatus.COMPLETED);
    exam.setUpdatedAt(LocalDateTime.now());
    examRepository.save(exam);

    /* ===================== CLEAR REDIS ===================== */
    redisExamResultService.deleteNormalExamResultsFromRedis(
            tenantId,
            userId,
            examId
    );

    /* ===================== RESPONSE ===================== */
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("message", "Exam submission confirmed and results saved successfully");
    response.put("examId", examId);
    response.put("totalMarks", totalMarks);
    response.put("resultsSavedCount", redisResults.size());
    response.put("submittedDateTime", finalResult.getSubmittedDateTime());

    // 🔥 ADD THIS
response.put(
    "chapters",
    chapterResults.stream()
        .map(this::convertChapterResultToMap)
        .collect(Collectors.toList())
);

 // Send messages to RabbitMQ AFTER this transaction commits,
 // so consumers always see the final state in exam_results.
 if (exam.getExamType() != null) {
     messageProducer.sendAllMessagesBatch(userId, examId);
 }

    return response;
}

private Map<String, Object> convertChapterResultToMap(ChapterResult cr) {
    Map<String, Object> map = new LinkedHashMap<>();

    map.put("chapter", Map.of(
            "id", cr.getChapter().getId(),
            "name", cr.getChapter().getName(),
            "subject", cr.getChapter().getSubject()
    ));

    map.put("timeSpent", cr.getTimeSpent());
    map.put("percentage", cr.getPercentage());
    map.put("examType", cr.getExamType());
    map.put("marks", cr.getMarks());
    map.put("aiAnalysis", cr.getAiAnalysis());
    map.put("totalMarks", cr.getTotalMarks());

    return map;
}

private String formatDuration(Duration duration) {
    if (duration == null) return "00:00:00";

    return String.format(
            "%02d:%02d:%02d",
            duration.toHours(),
            duration.toMinutesPart(),
            duration.toSecondsPart()
    );
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


@Override
@Transactional(readOnly = true)
public Map<String, Object> getFinalResults(Long examId, String subdomain) {

    /* ================= AUTH ================= */

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    User user = (User) auth.getPrincipal();

    /* ================= TENANT ================= */

    Tenant tenant = tenantRepository.findBySubdomain(subdomain)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

    /* ================= EXAM ================= */

    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new RuntimeException("Exam not found"));

    if (!exam.getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Exam does not belong to user");
    }

    if (exam.getStatus() != Exam.ExamStatus.COMPLETED &&
        exam.getStatus() != Exam.ExamStatus.ABORTED) {
        throw new RuntimeException("Exam not completed or aborted");
    }

    /* ================= FINAL RESULT ================= */

    ExamFinalResult finalResult =
            examFinalResultRepository
                    .findByExamId(examId)
                    .orElseThrow(() ->
                            new RuntimeException("Final result not found"));

    /* ================= CHAPTER RESULTS ================= */

    List<ChapterResult> chapters =
            chapterResultRepository.findByExamId(examId);

    if (chapters.isEmpty()) {
        throw new RuntimeException("No chapter results found");
    }

    /* ================= TOTAL PERCENTAGE ================= */

    int totalMarks = finalResult.getTotalMarks();

    int maxMarks = chapters.stream()
            .mapToInt(ChapterResult::getTotalMarks)
            .sum();

    double totalPercentage =
            maxMarks > 0 ? (double) totalMarks / maxMarks * 100 : 0;

    /* ================= RESPONSE ================= */

    Map<String, Object> response = new LinkedHashMap<>();

    response.put("totalTimeSpent", finalResult.getTotalTimeSpent());

    response.put(
            "chapters",
            chapters.stream()
                    .map(this::convertChapterResultToMap)
                    .toList()
    );

    response.put("submittedDateTime", finalResult.getSubmittedDateTime());
    response.put("examType", exam.getExamType());
    response.put("totalMarks", totalMarks);
    response.put("totalPercentage", Math.max(0, totalPercentage));

    /* ================= SWOT ================= */

    response.put("swot", buildSwotFromChapterResults(chapters));

    return response;
}



private Map<String, Object> buildSwotFromChapterResults(
    List<ChapterResult> chapters
) {
   
    Map<String, List<ChapterResult>> bySubject =
    chapters.stream()
            .collect(Collectors.groupingBy(
                    (ChapterResult c) -> String.valueOf(
                            c.getChapter().getSubject()
                    ).toUpperCase()
            ));


Map<String, Object> swot = new LinkedHashMap<>();

for (String subject : bySubject.keySet()) {
    List<ChapterResult> list = bySubject.get(subject);

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
    List<ChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildStrengths(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
        Map.of(subject, chapters.stream()
                .map(c -> parseDuration(c.getTimeSpent()))
                .reduce(Duration.ZERO, Duration::plus)),
        true
);

Object value = result.get(capitalize(subject));
return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
}


private Map<String, Object> buildWeaknessMessages(
    List<ChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildWeaknesses(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
        Map.of(subject, (int) chapters.stream().filter(c -> c.getMarks() == 0).count()),
        Map.of(subject, new HashMap<>())
);

Object value = result.get(capitalize(subject));
return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
}



private Map<String, Object> buildOpportunityMessages(
    List<ChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildOpportunities(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
        Map.of(subject, "BASIC")
);

Object value = result.get(capitalize(subject));
return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
}

private Map<String, Object> buildThreatMessages(
    List<ChapterResult> chapters,
    String subject
) {
Map<String, Object> result = buildThreats(
        Map.of(subject, chapters),
        Map.of(subject, chapters.stream().mapToInt(ChapterResult::getMarks).sum()),
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
    Map<String, List<ChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, Duration> subjectTimes,
    boolean isNegativeMarking
) {
    Map<String, Object> strengths = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        Duration timeSpent = subjectTimes.getOrDefault(subject, Duration.ZERO);
        double percentage = (double) totalScore / 180 * 100;
        List<ChapterResult> sortedChapters = chapters.stream()
            .sorted(Comparator.comparingDouble(ChapterResult::getPercentage).reversed())
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
            List<String> top2 = sortedChapters.stream().limit(2).map(ch ->ch.getChapter().getName()).toList();
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
            List<String> top3 = sortedChapters.stream().limit(3).map(ch -> ch.getChapter().getName()).toList();
            subjectStrength.put("topChapters", top3);
        }
        // 5. <50% but any chapter >70%
        if (percentage < 50) {
            List<String> highChapters = sortedChapters.stream()
                .filter(ch -> ch.getPercentage() > 70)
                .limit(3)
                .map(ch ->ch.getChapter().getName())
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
    Map<String, List<ChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, Integer> subjectUnansweredCounts,
    Map<String, Map<String, Integer>> chapterUnansweredCounts
) {
    Map<String, Object> weaknesses = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        double percentage = (double) totalScore / 180 * 100;
        Map<String, Object> subjectWeakness = new LinkedHashMap<>();
        List<String> messages = new ArrayList<>();
        List<String> lowestChapters = chapters.stream()
            .sorted(Comparator.comparingDouble(ChapterResult::getPercentage))
            .limit(3)
            .map(ch -> ch.getChapter().getName())
            .toList();


        // 1. <50% overall
        if (percentage < 50) {
            messages.add("Needs more focused attention in these chapters:");
            subjectWeakness.put("lowestChapters", lowestChapters);
            messages.add("Now's the time to rebuild confidence:\n📌 Prioritize targeted revision and take chapter-level practice tests to strengthen your fundamentals.");
        }
        // 2. >20% unanswered
        int unanswered = subjectUnansweredCounts.getOrDefault(subject, 0);
        int totalQuestions = chapters.stream().mapToInt(ChapterResult::getTotalMarks).sum() / 4;
        double unansweredRatio = totalQuestions > 0 ? (double) unanswered / totalQuestions : 0;
        if (unansweredRatio > 0.2) {
            messages.add("More than 20% of questions were left unanswered in this subject. This suggests hesitation, time management issues, or concept uncertainty.");
            messages.add("Recommendations:\n• Reattempt the full mock under timed conditions\n• Focus on strengthening weaker topics\n• Use Mark & Review strategies during timed practice");
        }
        // 3. Chapters >30% unanswered
        Map<String, Integer> chapterUnans = chapterUnansweredCounts.getOrDefault(subject, new HashMap<>());
        List<String> uncertainChapters = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : chapterUnans.entrySet()) {
            int chapterTotal = chapters.stream().filter(ch ->ch.getChapter().getName()
            .equals(entry.getKey()))
                .mapToInt(ChapterResult::getTotalMarks).sum() / 4;
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
    Map<String, List<ChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, String> subjectDifficulty
) {
    Map<String, Object> opportunities = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        double percentage = (double) totalScore / 180 * 100;
        Map<String, Object> subjectOpp = new LinkedHashMap<>();
        List<String> messages = new ArrayList<>();
        List<String> lowestChapters = chapters.stream()
            .sorted(Comparator.comparingDouble(ChapterResult::getPercentage))
            .limit(2)
            .map(ch ->ch.getChapter().getName()).toList();
            

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
    Map<String, List<ChapterResult>> subjectChapters,
    Map<String, Integer> subjectScores,
    Map<String, Duration> subjectTimes,
    Map<String, Map<String, Integer>> chapterWrongAnswers
) {
    Map<String, Object> threats = new LinkedHashMap<>();
    for (String subject : subjectChapters.keySet()) {
        List<ChapterResult> chapters = subjectChapters.get(subject);
        int totalScore = subjectScores.getOrDefault(subject, 0);
        Duration timeSpent = subjectTimes.getOrDefault(subject, Duration.ZERO);
        Map<String, Object> subjectThreat = new LinkedHashMap<>();
        List<String> messages = new ArrayList<>();
        List<String> highestNegChapters = chapters.stream()
        .sorted(Comparator.comparingInt(ChapterResult::getMarks))
        .limit(3)
        .map(ch -> ch.getChapter().getName())
        .toList();
    List<String> lowestChapters = chapters.stream()
        .sorted(Comparator.comparingDouble(ChapterResult::getPercentage))
        .limit(2)
        .map(ch -> ch.getChapter().getName())
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


} 
