package com.brihathi.Multi_Tenant.service;

import java.util.List;
import com.brihathi.Multi_Tenant.dto.LeadershipBoardTopScoreDTO;

public interface LeadershipBoardService {
    void createLeadershipBoardEntry(Long examId, Long userId);
    public List<LeadershipBoardTopScoreDTO > getTopTotalMarks(Long userId) ;
}
