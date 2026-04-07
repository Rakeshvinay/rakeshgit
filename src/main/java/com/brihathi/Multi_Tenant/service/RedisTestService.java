package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.entity.ScheduledExamResult;
import com.brihathi.Multi_Tenant.dto.UpdateQuestionRequestDTO;
import com.brihathi.Multi_Tenant.entity.ExamResult;
import com.brihathi.Multi_Tenant.repository.ScheduledExamResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.brihathi.Multi_Tenant.repository.ExamResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class RedisTestService {

    private static final Logger logger = LoggerFactory.getLogger(RedisTestService.class);

    private final ObjectMapper objectMapper;
    // private final ExamResultRepository examResultRepository2;
    private final HashOperations<String, String, String> hashOps;
    private final DefaultRedisScript<Long> timeUpdateScript;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ScheduledExamResultRepository scheduledExamResultRepository;
    @Autowired
    private ExamResultRepository examResultRepository;

    public RedisTestService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        this.timeUpdateScript = new DefaultRedisScript<>();
        this.timeUpdateScript.setScriptText(
            "local current = redis.call('GET', KEYS[1]) or '0' " +
            "local new = tostring(tonumber(current) + tonumber(ARGV[1])) " +
            "redis.call('SET', KEYS[1], new) " +
            "return tonumber(new)"
        );
        this.timeUpdateScript.setResultType(Long.class);
    }

    /* ===================== KEY BUILDER ===================== */

    private String resultKey(Long tenantId, Long userId, Long scheduledExamId) {
        return String.format(
            "tenant:%d:user:%d:scheduledExam:%d:results",
            tenantId, userId, scheduledExamId
        );
    }

    private String timeKey(Long tenantId, Long userId, Long scheduledExamId) {
        return String.format(
            "tenant:%d:user:%d:scheduledExam:%d:totalTime",
            tenantId, userId, scheduledExamId
        );
    }

    /* ===================== READ ===================== */

    public List<ScheduledExamResult> getRedisTestService(
            Long tenantId,
            Long userId,
            Long scheduledExamId
    ) {
        try {
            String key = resultKey(tenantId, userId, scheduledExamId);
            Map<String, String> results = hashOps.entries(key);

            if (results.isEmpty()) return null;

            List<ScheduledExamResult> examResults = new ArrayList<>();
            for (String json : results.values()) {
                examResults.add(
                    objectMapper.readValue(json, ScheduledExamResult.class)
                );
            }
            return examResults;
        } catch (Exception e) {
            logger.error("Redis fetch error", e);
            return null;
        }
    }

    /* ===================== SAVE ===================== */

    public void saveExamResultsToRedis(
            Long tenantId,
            Long userId,
            Long scheduledExamId,
            List<ScheduledExamResult> results
    ) {
        if (results == null || results.isEmpty()) return;

        try {
            String key = resultKey(tenantId, userId, scheduledExamId);
            Map<String, String> map = new HashMap<>();

            for (ScheduledExamResult r : results) {
                map.put(
                    r.getQid(),
                    objectMapper.writeValueAsString(r)
                );
            }
            hashOps.putAll(key, map);
        } catch (Exception e) {
            logger.error("Redis save error", e);
        }
    }

    /* ===================== UPDATE QUESTION ===================== */

    public void updateQuestionStatus(
            Long tenantId,
            Long userId,
            Long scheduledExamId,
            String qid,
            Map<String, Object> updates
    ) {
        try {
            String key = resultKey(tenantId, userId, scheduledExamId);
            String json = hashOps.get(key, qid);

            ScheduledExamResult result =
                json == null
                    ? scheduledExamResultRepository
                        .findByScheduledExamIdAndQid(scheduledExamId, qid)
                        .get(0)
                    : objectMapper.readValue(json, ScheduledExamResult.class);

            /* ---- time ---- */
            if (updates.containsKey("timeSpent")) {
                long seconds = Long.parseLong(updates.get("timeSpent").toString());
                Duration newDuration = Duration.ofSeconds(seconds);

                long diff =
                    newDuration.toSeconds() -
                    result.getDuration().toSeconds();

                if (diff != 0) {
                    redisTemplate.execute(
                        timeUpdateScript,
                        List.of(timeKey(tenantId, userId, scheduledExamId)),
                        String.valueOf(diff)
                    );
                }
                result.setDuration(newDuration);
            }

            /* ---- flags ---- */
            if (updates.containsKey("visited"))
                result.setVisited((Boolean) updates.get("visited"));

            if (updates.containsKey("answered"))
                result.setAnswered((Boolean) updates.get("answered"));

            if (updates.containsKey("markedForReview"))
                result.setMarkedForReview((Boolean) updates.get("markedForReview"));

            /* ---- answer + validation ---- */
            if (updates.containsKey("answerOption")) {
                String ans = (String) updates.get("answerOption");
                result.setAnswerOption(ans);

                if (ans == null) {
                    result.setValidateAnswer(null);
                    result.setMarks(null);
                } else if (ans.equals(result.getCorrectAnswerOption())) {
                    result.setValidateAnswer("CORRECT");
                    result.setMarks(4);
                } else {
                    result.setValidateAnswer("WRONG");
                    result.setMarks(0);
                }
            }

            hashOps.put(key, qid, objectMapper.writeValueAsString(result));

        } catch (Exception e) {
            logger.error("Update question failed", e);
            throw new RuntimeException(e);
        }
    }

    /* ===================== TOTAL TIME ===================== */

    public Duration getTotalTime(
            Long tenantId,
            Long userId,
            Long scheduledExamId
    ) {
        String val =
            redisTemplate.opsForValue()
                .get(timeKey(tenantId, userId, scheduledExamId));

        return val == null
            ? Duration.ZERO
            : Duration.ofSeconds(Long.parseLong(val));
    }

    /* ===================== DELETE ===================== */

    public void deleteExamResultsFromRedis(
            Long tenantId,
            Long userId,
            Long scheduledExamId
    ) {
        redisTemplate.delete(
            List.of(
                resultKey(tenantId, userId, scheduledExamId),
                timeKey(tenantId, userId, scheduledExamId)
            )
        );
    }


    public void saveScheduledExamResultsToRedis(
        String tenantId,
        String educatorId,
        Long userId,
        Long scheduledExamId,
        List<ScheduledExamResult> examResults
) {
    if (examResults == null || examResults.isEmpty()) {
        logger.warn("No scheduled exam results to save for userId {} examId {}", userId, scheduledExamId);
        return;
    }

    try {
        String key = String.format(
                "tenant:%s:educator:%s:user:%d:scheduledExam:%d:results",
                tenantId,
                educatorId,
                userId,
                scheduledExamId
        );

        Map<String, String> resultsMap = new HashMap<>();

        for (ScheduledExamResult result : examResults) {
            String json = objectMapper.writeValueAsString(result);
            resultsMap.put(result.getQid(), json);
        }

        hashOps.putAll(key, resultsMap);

        logger.info(
                "Saved {} scheduled exam results to Redis [key={}]",
                examResults.size(),
                key
        );

    } catch (Exception e) {
        logger.error(
                "Failed to save scheduled exam results to Redis for userId {} examId {}",
                userId,
                scheduledExamId,
                e
        );
        throw new RuntimeException("Redis save failed", e);
    }
}



public void initializeTotalTime(
    String tenantId,
    String educatorId,
    Long userId,
    Long scheduledExamId
) {
String timeKey = String.format(
        "tenant:%s:educator:%s:user:%d:scheduledExam:%d:totalTime",
        tenantId,
        educatorId,
        userId,
        scheduledExamId
);
redisTemplate.opsForValue().set(timeKey, "0");
}

public void saveScheduledExamResultsToRedis(
    Long tenantId,
    Long eduScheduledExamId,
    Long scheduledExamId,
    Long userId,
    List<ScheduledExamResult> results
) {
String key = String.format(
    "tenant:%d:eduExam:%d:exam:%d:user:%d",
    tenantId, eduScheduledExamId, scheduledExamId, userId
);

Map<String, String> data = new HashMap<>();

for (ScheduledExamResult r : results) {
    try {
        data.put(r.getQid(), objectMapper.writeValueAsString(r));
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

redisTemplate.opsForHash().putAll(key, data);
}

// public void startNormalExam(Long tenantId, Long examId, Long userId) {

//     String redisKey = String.format(
//             "NORMAL_EXAM:%d:%d:%d",
//             tenantId,
//             examId,
//             userId
//     );

//     Map<String, Object> data = new HashMap<>();
//     data.put("tenantId", tenantId);
//     data.put("examId", examId);
//     data.put("userId", userId);

//     redisTemplate.opsForHash().putAll(redisKey, data);

//     // Optional TTL if you already use it
//     // redisTemplate.expire(redisKey, Duration.ofHours(4));

  

// }

/* ===================== NORMAL EXAM ===================== */

private String normalExamResultKey(Long tenantId, Long userId, Long examId) {
    return String.format(
        "tenant:%d:user:%d:exam:%d:results",
        tenantId, userId, examId
    );
}

public void saveNormalExamResultsToRedis(
        Long tenantId,
        Long userId,
        Long examId,
        List<ExamResult> results
) {

    if (results == null || results.isEmpty()) {
        logger.warn(
            "No normal exam results to save for userId {} examId {}",
            userId, examId
        );
        return;
    }

    try {
        String key = normalExamResultKey(tenantId, userId, examId);
        Map<String, String> map = new HashMap<>();

        for (ExamResult r : results) {
            map.put(
                r.getQid(),
                objectMapper.writeValueAsString(r)
            );
        }

        hashOps.putAll(key, map);

        logger.info(
            "Saved {} normal exam results to Redis [key={}]",
            results.size(),
            key
        );

    } catch (Exception e) {
        logger.error("Redis save failed for normal exam", e);
        throw new RuntimeException("Redis save failed", e);
    }
}



/* ===================== NORMAL EXAM DELETE ===================== */

public void deleteNormalExamResultsFromRedis(
    Long tenantId,
    Long userId,
    Long examId
) {

String key = String.format(
        "tenant:%d:user:%d:exam:%d:results",
        tenantId, userId, examId
);

redisTemplate.delete(key);
}




public ExamResult updateNormalExamQuestion(
    Long tenantId,
    Long userId,
    Long examId,
    UpdateQuestionRequestDTO request
) {

try {
    String key = String.format(
            "tenant:%d:user:%d:exam:%d:results",
            tenantId, userId, examId
    );

    String json = hashOps.get(key, request.getQuestionId());

    ExamResult result =
            json == null
                    ? examResultRepository
                        .findByExamIdAndUserIdAndTenantIdAndQid(
                                examId,
                                userId,
                                tenantId,
                                request.getQuestionId()
                        )
                        .orElseThrow(() -> new RuntimeException("Question not found"))
                    : objectMapper.readValue(json, ExamResult.class);

    /* ---------- FLAGS ---------- */
    if (request.getVisited() != null)
        result.setVisited(request.getVisited());

    if (request.getAnswered() != null)
        result.setAnswered(request.getAnswered());

    if (request.getMarkedForReview() != null)
        result.setMarkedForReview(request.getMarkedForReview());

    /* ---------- DURATION ---------- */
    if (request.getDuration() != null)
        result.setDuration(Duration.ofSeconds(request.getDuration()));

    /* ---------- ANSWER ---------- */
    if (request.getAnswerOption() != null) {
        result.setAnswerOption(request.getAnswerOption());

        if (request.getAnswerOption().equals(result.getCorrectAnswerOption())) {
            result.setValidateAnswer("CORRECT");
            result.setMarks(4);
        } else {
            result.setValidateAnswer("WRONG");
            result.setMarks(0);
        }
    }

    hashOps.put(
            key,
            result.getQid(),
            objectMapper.writeValueAsString(result)
    );

    return result;

} catch (Exception e) {
    throw new RuntimeException("Failed to update question", e);
}
}




public Duration getNormalExamTotalTime(
    Long tenantId,
    Long userId,
    Long examId
) {
String key = String.format(
        "tenant:%d:user:%d:exam:%d:totalTime",
        tenantId, userId, examId
);

String val = redisTemplate.opsForValue().get(key);

return val == null
        ? Duration.ZERO
        : Duration.ofSeconds(Long.parseLong(val));
}



public List<ExamResult> getNormalExamResultsFromRedis(
    Long tenantId,
    Long userId,
    Long examId
) {
try {
    String key = String.format(
            "tenant:%d:user:%d:exam:%d:results",
            tenantId, userId, examId
    );

    Map<String, String> results = hashOps.entries(key);

    if (results == null || results.isEmpty()) {
        return null;
    }

    List<ExamResult> examResults = new ArrayList<>();

    for (String json : results.values()) {
        examResults.add(
                objectMapper.readValue(json, ExamResult.class)
        );
    }

    return examResults;

} catch (Exception e) {
    logger.error("Failed to fetch normal exam results from Redis", e);
    return null;
}
}

}
