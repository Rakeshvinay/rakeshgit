package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.ScheduledExamResult;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionAnalyticsRepository extends JpaRepository<ScheduledExamResult, Long> {

    @Query(value = """
       SELECT r.qid, COUNT(*)
FROM scheduled_examresults r
WHERE r.answered = true
AND r.answer_option IS NOT NULL
AND r.correct_answer_option IS NOT NULL
AND r.answer_option <> r.correct_answer_option
AND (:subject IS NULL OR r.subject = :subject)
AND (:branch IS NULL OR r.branch_id = :branch)
AND (:batch IS NULL OR r.batch_id = :batch)
GROUP BY r.qid
ORDER BY COUNT(*) DESC
LIMIT 25

    """, nativeQuery = true)
    List<Object[]> findMostWrong(String subject, String branch, String batch);

    @Query(value = """
       SELECT r.qid, COUNT(*)
FROM scheduled_examresults r
WHERE r.answered = false
AND (:subject IS NULL OR r.subject = :subject)
AND (:branch IS NULL OR r.branch_id = :branch)
AND (:batch IS NULL OR r.batch_id = :batch)
GROUP BY r.qid
ORDER BY COUNT(*) DESC
LIMIT 25

    """, nativeQuery = true)
    List<Object[]> findMostSkipped(String subject, String branch, String batch);

    @Query(value = """
       SELECT r.qid, AVG(r.duration)
FROM scheduled_examresults r
WHERE r.answered = true
AND (:subject IS NULL OR r.subject = :subject)
AND (:branch IS NULL OR r.branch_id = :branch)
AND (:batch IS NULL OR r.batch_id = :batch)
GROUP BY r.qid
ORDER BY AVG(r.duration) DESC
LIMIT 25

    """, nativeQuery = true)
    List<Object[]> findMostTimeConsuming(String subject, String branch, String batch);
}
