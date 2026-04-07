// package com.brihathi.Multi_Tenant.service;
// import java.util.List;
// import com.brihathi.Multi_Tenant.dto.LeadershipBoardTopScoreDTO;

// public interface ScheduledLeadershipBoardService {
//     void createLeadershipBoardEntry(Long examId, Long userId);
//     public List<LeadershipBoardTopScoreDTO > getTopTotalMarks(Long userId) ;
// }
package com.brihathi.Multi_Tenant.service;

import java.util.List;

public interface ScheduledLeadershipBoardService {

    void createLeadershipBoardEntry(Long eduScheduledExamId, Long userId);

    List<Object[]> getTop5AndCurrentUser(Long eduScheduledExamId, Long userId);
}
