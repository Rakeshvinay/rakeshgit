
package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.entity.Exam;
// import com.brihathi.Multi_Tenant.entity.ExamResult;
import com.brihathi.Multi_Tenant.entity.QuestionPublic;
import com.brihathi.Multi_Tenant.entity.QuestionTenant;
import com.brihathi.Multi_Tenant.dto.AbortExamResponseDTO;
import com.brihathi.Multi_Tenant.dto.QuestionStatusDTO;
import com.brihathi.Multi_Tenant.dto.UpdateQuestionRequestDTO;
import com.brihathi.Multi_Tenant.dto.EndExamSummaryDTO;
import com.brihathi.Multi_Tenant.dto.CreateNormalExamRequest;
import com.brihathi.Multi_Tenant.dto.NormalExamResponse;
import com.brihathi.Multi_Tenant.dto.StartExamResponseDTO;
import com.brihathi.Multi_Tenant.dto.ConfirmSubmissionResponseDTO;
import com.brihathi.Multi_Tenant.enums.Subject;
import com.brihathi.Multi_Tenant.enums.Difficulty;
 
import java.util.List;
import java.util.Map;
import java.util.Optional;
 
public interface ExamService {
    Exam createExam(Exam exam);
    // Optional<Exam> getExamById(Long examId);
    // List<Exam> getExamsByUserId(Long userId);
    // List<Exam> getExamsByUserIdAndStatus(Long userId, Exam.ExamStatus status);
    // Exam updateExam(Exam exam);
    // void deleteExam(Long examId);
    // Map<String, Object> startExam(Long examId, Long userId);
    // Map<String, Object> endExam(Long examId, Long userId);
    // Exam abortExam(Long examId, Long userId);
    // String getSubjectId(String subject);
    // // Optional<List<ExamResult>> getExamResults(Long examId);
    // List<QuestionPublic> generateRandomQuestions(Exam exam);
    // List<QuestionPublic> getRandomQuestionsBySubjectAndChapters(
    //     Subject subject,
    //     List<String> chapterIds,
    //     Difficulty difficulty,
    //     String grade,
    //     int limit
    // );
    // Map<String, Object> createAndStartExam(Long userId, Subject subject, List<String> chapterNames, Difficulty difficulty, String grade);
   
    // // New methods
    // Map<String, Object> getExamFinalResults(Long examId, Long userId);
    // Map<String, Object> generateExamResults();
    // Map<String, Object> getRandomQuestions(Map<String, Object> request);
    // Exam updateExamStartTime(Long examId, Long userId);
    // Map<String, Object> getGeneratedQuestionIds(Long examId);
    // // Map<String, Object> verifyRedisData(Long examId);
    // // Map<String, Object> checkRedisHealth();
    // // Map<String, Object> getUserExams(Long userId, String timeframe);
    // // Map<String, Object> getUserExamCount(Long userId, Long examId);
    // Map<String, Object> confirmExamSubmission(Long examId, Long userId);
    Map<String, Object> getExamInfo(String subdomain);
    // Map<String, Object> getQuestionByQid(String qid);
    // Map<String, Object> updateQuestion(Long examId, Map<String, Object> questionUpdate);
    // Map<String, Object> getExamStatistics(Long examId, Long userId);
    // List<Map<String, Object>> getChapterWiseMarksBySubject(Long examId, String subjectName, Long userId);


    NormalExamResponse createNormalExam(   CreateNormalExamRequest request, String subdomain);

    StartExamResponseDTO startNormalExam(Long examId, String subdomain);

    Map<String, Object> getQuestionByQid(String qid,String subdomain);
    Map<String, Object> getGeneratedQids(Long examId, String subdomain);

    AbortExamResponseDTO abortExam(Long examId, String subdomain);

    QuestionStatusDTO updateQuestion( Long examId,  UpdateQuestionRequestDTO request, String subdomain);


    EndExamSummaryDTO endNormalExam(Long examId, String subdomain);

    Map<String, Object> confirmSubmission( Long examId,  String subdomain);

    public Map<String, Object> getFinalResults(Long examId, String subdomain);





}
 
 