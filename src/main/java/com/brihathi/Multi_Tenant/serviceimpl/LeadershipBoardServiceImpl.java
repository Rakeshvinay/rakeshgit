package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.Exam;
import com.brihathi.Multi_Tenant.entity.ExamFinalResult;
import com.brihathi.Multi_Tenant.entity.LeadershipBoard;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.repository.ExamFinalResultRepository;
import com.brihathi.Multi_Tenant.repository.ExamRepository;
import com.brihathi.Multi_Tenant.repository.LeadershipBoardRepository;
import com.brihathi.Multi_Tenant.repository.UserRepository;
import com.brihathi.Multi_Tenant.repository.ScorePredictorRepository;
import com.brihathi.Multi_Tenant.entity.ScorePredictor;
import com.brihathi.Multi_Tenant.service.LeadershipBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.brihathi.Multi_Tenant.dto.LeadershipBoardTopScoreDTO;
  
@Service
public class LeadershipBoardServiceImpl implements LeadershipBoardService {
   
    @Autowired
    private ExamRepository examRepository;
   
    @Autowired
    private ExamFinalResultRepository examFinalResultRepository;
   
    @Autowired
    private LeadershipBoardRepository leadershipBoardRepository;
   
    @Autowired
    private UserRepository userRepository;
   
    @Autowired
    private ScorePredictorRepository scorePredictorRepository;

    @Autowired
    private com.brihathi.Multi_Tenant.repository.ChapterWiseResultRepository chapterWiseResultRepository;
 
    @Override
    @Transactional
    public void createLeadershipBoardEntry(Long examId, Long userId) {
        // Get the exam and user
       Exam exam = examRepository.findByExamIdAndUser_UserId(examId, userId)
        .orElseThrow(() -> new RuntimeException("Exam not found for ID: " + examId + " and User ID: " + userId));

       User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found for ID: " + userId)); 
        // Fetch all chapter-wise results for this user and exam
        List<com.brihathi.Multi_Tenant.entity.ChapterWiseResult> chapterResults = chapterWiseResultRepository.findByUser_UserIdAndExam_ExamId(userId, examId);
        Integer predictedScore = 0;
        // System.out.println("chapterResults: " + chapterResults);
        if (!chapterResults.isEmpty()) {
            // Sum the marks for all chapter-wise results for this exam
            predictedScore = chapterResults.stream().mapToInt(cr -> cr.getMarks() != null ? cr.getMarks() : 0).sum();
            // System.out.println("Calculated predictedScore: " + predictedScore);
        }
        
        // Get all exam final results to calculate ranking
        List<ExamFinalResult> allResults = examFinalResultRepository.findAll();
       
        // Sort by total marks in descending order to get ranking
        List<ExamFinalResult> sortedResults = allResults.stream()
            .sorted((r1, r2) -> Integer.compare(r2.getTotalMarks(), r1.getTotalMarks()))
            .collect(Collectors.toList());
       
        // Find the rank of current user
        int rank = 1;
        for (ExamFinalResult result : sortedResults) {
            if (result.getExamId().equals(examId)) {
                break;
            }
            rank++;
        }
        ExamFinalResult finalResult = examFinalResultRepository.findByExamIdAndUserId(examId,userId).orElse(null);
        Integer totalMarks = (finalResult != null) ? finalResult.getTotalMarks() : 0;
       
        // Create leadership board entry
        LeadershipBoard leadershipBoard = LeadershipBoard.builder()
            .uuid(UUID.randomUUID())
            .user(user)
            .tenantId(exam.getTenantId())
            .examid(examId)
            .totalmarks(totalMarks)
            .build();
        
        // System.out.println("Saving LeadershipBoard with examid: " + examId + ", predicted_score: " + predictedScore);
        leadershipBoardRepository.save(leadershipBoard);
    }
    @Override
    public List<LeadershipBoardTopScoreDTO> getTopTotalMarks(Long userId) {
        return leadershipBoardRepository.getTop5AndCurrentUser(userId);
    }

}
