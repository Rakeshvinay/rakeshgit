package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScorePredictor;
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.dto.PredictedRankDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface ScorePredictorRepository extends JpaRepository<ScorePredictor, Long> {
    //Optional<ScorePredictor> findByUserId(Long userId);
    Optional<ScorePredictor> findByUser(User user);
    Optional<ScorePredictor> findByUserUserId(Long userId);



    @Query(value = """
        SELECT 
            predicted_score AS predictedScore,
            predicted_rank AS predictedRank,
            need_to_improve AS needToImprove,
            good_at AS goodAt,
            no_of_exams AS noOfExams
        FROM 
            score_predictor
        WHERE 
            user_id = :userId
        """, nativeQuery = true)
    PredictedRankDTO getPredictedRankForUser(@Param("userId") Long userId);

}
