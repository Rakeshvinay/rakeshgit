// package com.brihathi.Multi_Tenant.repository;

// import com.brihathi.Multi_Tenant.entity.ScheduledScorePredictor;
// import com.brihathi.Multi_Tenant.entity.User;
// import com.brihathi.Multi_Tenant.dto.PredictedRankDTO;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import java.util.Optional;

// @Repository
// public interface ScheduledScorePredictorRepository extends JpaRepository<ScheduledScorePredictor, Long> {
//     //Optional<ScorePredictor> findByUserId(Long userId);
//     //Optional<ScheduledScorePredictorMains> findByUser(User user);
//     Optional<ScheduledScorePredictor> findByUserId(Long userId);



//     @Query(value = """
//         SELECT 
//             predicted_score AS predictedScore,
//             predicted_rank AS predictedRank,
//              percentage AS Percentage,
//             need_to_improve AS needToImprove,
//             good_at AS goodAt,
//             no_of_exams AS noOfExams
//         FROM 
//             scheduled_exams_score_predictor
//         WHERE 
//             user_id = :userId
//         """, nativeQuery = true)
//     PredictedRankDTO getPredictedRankForUser(@Param("userId") Long userId);

// }
package com.brihathi.Multi_Tenant.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brihathi.Multi_Tenant.entity.ScheduledScorePredictor;

@Repository
public interface ScheduledScorePredictorRepository
        extends JpaRepository<ScheduledScorePredictor, UUID> {

    Optional<ScheduledScorePredictor> findByUserId(Long userId);


    @Query("""
SELECT s
FROM ScheduledScorePredictor s
WHERE s.userId = :userId
AND s.tenantId = :tenantId
ORDER BY s.updatedAt DESC
""")
List<ScheduledScorePredictor> findLatest(Long userId, Long tenantId);



 // 🔥 THIS IS THE NEW METHOD YOU WERE MISSING
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("SELECT s FROM ScheduledScorePredictor s WHERE s.userId = :userId")
 Optional<ScheduledScorePredictor> findByUserIdForUpdate(@Param("userId") Long userId);


}
