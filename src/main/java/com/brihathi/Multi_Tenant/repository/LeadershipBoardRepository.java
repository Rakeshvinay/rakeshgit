
package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.LeadershipBoard;
import com.brihathi.Multi_Tenant.dto.LeadershipBoardTopScoreDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
import java.util.UUID;
 
@Repository
public interface LeadershipBoardRepository extends JpaRepository<LeadershipBoard, UUID> {

       @Query(value = """
        WITH ranked AS (
            SELECT
                lb.user_id,
                u.name,
                MAX(lb.total_marks) AS maxTotalMarks,
                RANK() OVER (ORDER BY MAX(lb.total_marks) DESC) AS rank
            FROM leadership_board lb
            JOIN users u ON lb.user_id = u.user_id
            GROUP BY lb.user_id, u.name
        )
        SELECT * FROM ranked
        WHERE user_id IN (
            SELECT user_id FROM (
                SELECT user_id FROM ranked ORDER BY rank LIMIT 5
            ) AS top_users
            UNION
            SELECT :userId
        )
        ORDER BY rank
        """, nativeQuery = true)
    List<LeadershipBoardTopScoreDTO> getTop5AndCurrentUser(@Param("userId") Long userId);
}
 
 
