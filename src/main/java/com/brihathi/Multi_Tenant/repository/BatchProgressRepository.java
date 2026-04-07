package com.brihathi.Multi_Tenant.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
 
public interface BatchProgressRepository extends JpaRepository<com.brihathi.Multi_Tenant.entity.User, Long> {
 
    @Query(value = """
    SELECT
        u.branch,
        u.batch,
 
        COUNT(DISTINCT u.user_id) AS totalStudents,
 
        ROUND(AVG(sfr.total_marks),2) AS avgMarks,
 
        ROUND(
            SUM(CASE WHEN ser.validate_answer = 'correct' THEN 1 ELSE 0 END) * 100.0
            / NULLIF(COUNT(ser.id),0), 2
        ) AS accuracy,
 
        COUNT(DISTINCT CASE
            WHEN ese.exam_status = 'COMPLETED'
            THEN ese.edu_scheduled_exam_id
        END) AS testsConducted
 
    FROM users u
 
    LEFT JOIN scheduled_exam_final_results sfr
        ON sfr.user_id = u.user_id
 
    LEFT JOIN scheduled_examresults ser
        ON ser.user_id = u.user_id
 
    LEFT JOIN educators_scheduled_exams ese
        ON ese.branch = u.branch
        AND ese.batch = u.batch
 
    WHERE u.tenant_id = :tenantId AND u.branch = :branch
    AND (:batch IS NULL OR u.batch = :batch)
 
    GROUP BY u.branch, u.batch
""", nativeQuery = true)
List<Object[]> getBatchProgress(
    @Param("tenantId") Long tenantId,
        @Param("branch") String branch,
        @Param("batch") String batch
);
 
@Query(value = """
        SELECT
            ese.edu_scheduled_exam_id,
            ese.branch,
            ese.batch,
            ese.scheduled_time,
            ese.scheduled_date,
 
            COUNT(se.user_id) AS totalScheduled,
 
            SUM(CASE WHEN se.status = 'COMPLETED' THEN 1 ELSE 0 END) AS totalAttempted
 
        FROM educators_scheduled_exams ese
 
        JOIN scheduled_exams se
            ON se.edu_scheduled_exam_id = ese.edu_scheduled_exam_id
 
       WHERE ese.tenant_id = :tenantId   -- 🔥 TENANT FILTER
    AND ese.branch = :branch
    AND (:batch IS NULL OR ese.batch = :batch)
 
        GROUP BY
            ese.edu_scheduled_exam_id,
            ese.branch,
            ese.batch,
            ese.scheduled_time,
            ese.scheduled_date
 
        ORDER BY ese.scheduled_time ASC
    """, nativeQuery = true)
    List<Object[]> getBatchExamAttendance(
        @Param("tenantId") Long tenantId,
            @Param("branch") String branch,
            @Param("batch") String batch
    );
   
 
 
    @Query(value = """
SELECT
    u.user_id,
    u.name,
    u.enrollment_id,
    u.branch,
    u.batch,
 
    COUNT(se.edu_scheduled_exam_id) AS scheduled,
 
    SUM(CASE WHEN se.status = 'COMPLETED' THEN 1 ELSE 0 END) AS attempted
 
FROM users u
JOIN scheduled_exams se ON se.user_id = u.user_id
 
WHERE u.tenant_id = :tenantId
AND u.branch = :branch
AND u.batch = :batch
 
GROUP BY u.user_id, u.name, u.enrollment_id, u.branch, u.batch
 
ORDER BY (SUM(CASE WHEN se.status = 'COMPLETED' THEN 1 ELSE 0 END) * 1.0 /
         NULLIF(COUNT(se.edu_scheduled_exam_id),0)) ASC
 
LIMIT 3
""", nativeQuery = true)
List<Object[]> getLowParticipationStudents(Long tenantId, String branch, String batch);
 
 
 
 
 
@Query(value = """
SELECT
    u.user_id,
    u.name,
    u.enrollment_id,
    u.branch,
    u.batch,
 
    ROUND(AVG(sfr.total_marks),2) AS avgMarks
 
FROM users u
JOIN scheduled_exam_final_results sfr ON sfr.user_id = u.user_id
 
WHERE u.tenant_id = :tenantId
AND u.branch = :branch
AND u.batch = :batch
 
GROUP BY u.user_id, u.name, u.enrollment_id, u.branch, u.batch
 
ORDER BY avgMarks ASC
LIMIT 3
""", nativeQuery = true)
List<Object[]> getLowScoreStudents(Long tenantId, String branch, String batch);
 
 
 
 
@Query(value = """
SELECT
    u.user_id,
    u.name,
    u.enrollment_id,
    COUNT(se.edu_scheduled_exam_id) AS scheduled,
    SUM(CASE WHEN se.status = 'COMPLETED' THEN 1 ELSE 0 END) AS attempted
 
FROM users u
JOIN scheduled_exams se ON se.user_id = u.user_id
 
WHERE u.tenant_id = :tenantId
AND u.branch = :branch
AND u.batch = :batch
 
GROUP BY u.user_id, u.name, u.enrollment_id
 
HAVING
    (SUM(CASE WHEN se.status = 'COMPLETED' THEN 1 ELSE 0 END) * 100.0
     / NULLIF(COUNT(se.edu_scheduled_exam_id),0)) < 60
""", nativeQuery = true)
List<Object[]> findLowParticipation(Long tenantId, String branch, String batch);
 
 
 
@Query(value = """
SELECT
    u.user_id,
    u.name,
    u.enrollment_id,
    ROUND(AVG(ser.marks),2) AS avgMarks
 
FROM users u
JOIN scheduled_examresults ser ON ser.user_id = u.user_id
 
WHERE u.tenant_id = :tenantId
AND u.branch = :branch
AND u.batch = :batch
AND ser.marks < 0
 
GROUP BY u.user_id, u.name, u.enrollment_id
 
HAVING ROUND(AVG(ser.marks),2) < -10
""", nativeQuery = true)
List<Object[]> findHighNegativeMarks(Long tenantId, String branch, String batch);
 
 
 
 
@Query(value = """
WITH last3 AS (
    SELECT
        sfr.user_id,
        sfr.total_marks,
        ROW_NUMBER() OVER (PARTITION BY sfr.user_id ORDER BY sfr.submitted_date_time DESC) rn
    FROM scheduled_exam_final_results sfr
    WHERE sfr.tenant_id = :tenantId
)
SELECT
    u.user_id,
    u.name,
    u.enrollment_id,
    MAX(CASE WHEN rn = 1 THEN total_marks END) AS latest,
    MAX(CASE WHEN rn = 3 THEN total_marks END) AS thirdLatest
 
FROM last3 l
JOIN users u ON u.user_id = l.user_id
 
WHERE u.branch = :branch
AND u.batch = :batch
AND rn <= 3
 
GROUP BY u.user_id, u.name, u.enrollment_id
 
HAVING
    ( (MAX(CASE WHEN rn = 3 THEN total_marks END) -
       MAX(CASE WHEN rn = 1 THEN total_marks END)) * 100.0
      / NULLIF(MAX(CASE WHEN rn = 3 THEN total_marks END),0)
    ) > 20
""", nativeQuery = true)
List<Object[]> findFallingScore(Long tenantId, String branch, String batch);
 
}
 
 