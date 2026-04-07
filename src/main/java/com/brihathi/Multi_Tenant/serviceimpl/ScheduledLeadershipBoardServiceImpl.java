// package com.brihathi.Multi_Tenant.serviceimpl;
 
// import com.brihathi.Multi_Tenant.entity.ScheduledExam;
// import com.brihathi.Multi_Tenant.entity.ScheduledExamFinalResult;
// import com.brihathi.Multi_Tenant.entity.ScheduledLeadershipBoard;
// import com.brihathi.Multi_Tenant.entity.User;
// import com.brihathi.Multi_Tenant.entity.ScheduledChapterWiseResult;
// import com.brihathi.Multi_Tenant.repository.ScheduledExamFinalResultRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledExamRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledLeadershipBoardRepository;
// import com.brihathi.Multi_Tenant.repository.UserRepository;
// import com.brihathi.Multi_Tenant.repository.ScheduledScorePredictorRepository;
// import com.brihathi.Multi_Tenant.entity.ScheduledScorePredictor;
// import com.brihathi.Multi_Tenant.service.ScheduledLeadershipBoardService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;
// import com.brihathi.Multi_Tenant.dto.LeadershipBoardTopScoreDTO;
// import com.brihathi.Multi_Tenant.repository.ScheduledChapterWiseResultRepository;
// @Service
// public class ScheduledLeadershipBoardServiceImpl implements ScheduledLeadershipBoardService {
   
//     @Autowired
//     private ScheduledExamRepository scheduledExamRepository;
   
//     @Autowired
//     private ScheduledExamFinalResultRepository scheduledExamFinalResultRepository;
   
//     @Autowired
//     private ScheduledLeadershipBoardRepository leadershipBoardRepository;
   
//     @Autowired
//     private UserRepository userRepository;
   
//     @Autowired
//     private ScheduledScorePredictorRepository scheduledScorePredictorRepository;

//     @Autowired
//     private ScheduledChapterWiseResultRepository scheduledChapterWiseResultRepository;
 
//     @Override
//     @Transactional
//     public void createLeadershipBoardEntry(Long eduScheduledExamId, Long userId) {
//         // Get the exam and user
//         ScheduledExam exam = scheduledExamRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
//         .orElseThrow(() -> new RuntimeException("Exam not found for ID: " + eduScheduledExamId + " and User ID: " + userId));

//        User user = userRepository.findById(userId)
//         .orElseThrow(() -> new RuntimeException("User not found for ID: " + userId)); 
//         // Fetch all chapter-wise results for this user and exam
//         List<ScheduledChapterWiseResult> chapterResults = scheduledChapterWiseResultRepository.findByUserIdAndEduScheduledExamId(userId, eduScheduledExamId);
//         Integer predictedScore = 0;
//         // System.out.println("chapterResults: " + chapterResults);
//         if (!chapterResults.isEmpty()) {
//             // Sum the marks for all chapter-wise results for this exam
//             predictedScore = chapterResults.stream().mapToInt(cr -> cr.getMarks() != null ? cr.getMarks() : 0).sum();
//             // System.out.println("Calculated predictedScore: " + predictedScore);
//         }
        
//         // Get all exam final results to calculate ranking
//         List<ScheduledExamFinalResult> allResults = scheduledExamFinalResultRepository.findAll();
       
//         // Sort by total marks in descending order to get ranking
//         List<ScheduledExamFinalResult> sortedResults = allResults.stream()
//             .sorted((r1, r2) -> Integer.compare(r2.getTotalMarks(), r1.getTotalMarks()))
//             .collect(Collectors.toList());
       
//         // Find the rank of current user
//         int rank = 1;
//         for (ScheduledExamFinalResult result : sortedResults) {
//             if (result.getEduScheduledExamId().equals(eduScheduledExamId)) {
//                 break;
//             }
//             rank++;
//         }
//         ScheduledExamFinalResult finalResult = scheduledExamFinalResultRepository.findByEduScheduledExamIdAndUserId(eduScheduledExamId,userId).orElse(null);
//         Integer totalMarks = (finalResult != null) ? finalResult.getTotalMarks() : 0;
       
//         // Create leadership board entry
//         ScheduledLeadershipBoard leadershipBoard = ScheduledLeadershipBoard.builder()
//             .uuid(UUID.randomUUID())
//             .userId(userId)
//             .examid(eduScheduledExamId)
//             .educatorId(exam.getEducatorId())
//             .eduScheduledExamId(eduScheduledExamId)
//             .scheduledExamId(exam.getScheduledExamId())
//             .tenantId(exam.getTenantId())
//             .batchId(Long.parseLong(exam.getBatchId()))
//             .branchId(Long.parseLong(exam.getBranchId()))
//             .totalmarks(totalMarks)
//             .build();
        
//         // System.out.println("Saving LeadershipBoard with examid: " + examId + ", predicted_score: " + predictedScore);
//         leadershipBoardRepository.save(leadershipBoard);
//     }
//     @Override
//     public List<LeadershipBoardTopScoreDTO> getTopTotalMarks(Long userId) {
//         return leadershipBoardRepository.getTop5AndCurrentUser(userId);
//     }

// }


package com.brihathi.Multi_Tenant.serviceimpl;

import com.brihathi.Multi_Tenant.entity.*;
import com.brihathi.Multi_Tenant.repository.*;
import com.brihathi.Multi_Tenant.service.ScheduledLeadershipBoardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ScheduledLeadershipBoardServiceImpl
        implements ScheduledLeadershipBoardService {
                @Autowired
                private ScheduledExamFinalResultRepository finalResultRepository;
                
    @Autowired
    private ScheduledExamRepository scheduledExamRepository;

    @Autowired
    private ScheduledChapterWiseResultRepository chapterWiseResultRepository;

    @Autowired
    private ScheduledLeadershipBoardRepository leadershipBoardRepository;

    /* =====================================================
       CREATE LEADERSHIP BOARD ENTRY (SCHEDULED)
       ===================================================== */

    @Override
    @Transactional
    public void createLeadershipBoardEntry(Long eduScheduledExamId, Long userId) {

        // 1️⃣ Fetch scheduled exam
        ScheduledExam scheduledExam =
        scheduledExamRepository
                .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Scheduled exam not found for user: " + userId)
                );


                        Long scheduledExamId = scheduledExam.getScheduledExamId();

        // 2️⃣ Idempotency check
        if (leadershipBoardRepository
                .findByScheduledExamIdAndUserId(scheduledExamId, userId)
                .isPresent()) {
            return;
        }

        // ScheduledExam scheduledExam = scheduledExamRepository
        // .findByEduScheduledExamIdAndUserId(eduScheduledExamId, userId)
        // .orElseThrow(() -> new RuntimeException("Scheduled exam not found"));

// Long scheduledExamId = scheduledExam.getScheduledExamId();

        // 3️⃣ Fetch chapter-wise results
        List<ScheduledChapterWiseResult> chapterResults =
                chapterWiseResultRepository
                        .findByScheduledExamIdAndUserId(
                                scheduledExamId, userId
                        );

        // 4️⃣ Calculate total marks (same spirit as old code)
        // int totalMarks = chapterResults.stream()
        //         .mapToInt(r -> r.getMarks() != null ? r.getMarks() : 0)
        //         .sum();
        // 3️⃣ Fetch FINAL EXAM RESULT
ScheduledExamFinalResult finalResult =
finalResultRepository
        .findByScheduledExamIdAndUserId(scheduledExamId, userId)
        .orElseThrow(() -> new RuntimeException("Final result not found"));

// 4️⃣ Total marks comes directly from final result table
int totalMarks = finalResult.getTotalMarks() != null
? finalResult.getTotalMarks()
: 0;


        // 5️⃣ Build entity
        ScheduledLeadershipBoard board =
                ScheduledLeadershipBoard.builder()
                        .uuid(UUID.randomUUID())
                        .scheduledExamId(scheduledExam.getScheduledExamId())
                        .eduScheduledExamId(eduScheduledExamId)
                        .userId(userId)
                        .tenantId(scheduledExam.getTenantId())
                        .educatorId(scheduledExam.getEducatorId())
                        .branchId(scheduledExam.getBranchId())
                        .batchId(scheduledExam.getBatchId())
                        .examid(scheduledExam.getScheduledExamId())
                        .totalmarks(totalMarks)
                        .build();

        // 6️⃣ Save
        leadershipBoardRepository.save(board);
    }

    /* =====================================================
       DASHBOARD QUERY
       ===================================================== */

    @Override
    public List<Object[]> getTop5AndCurrentUser(Long eduScheduledExamId, Long userId) {
        return leadershipBoardRepository
                .getTop5AndCurrentUser(eduScheduledExamId, userId);
    }
}

