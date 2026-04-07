package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
 
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
@Service
@RequiredArgsConstructor
public class MysteryBoxDataHolder {
 
    private final Map<Long, List<SubjectQuestionCountDTO>> deleteCountsMap = new ConcurrentHashMap<>();
    private final Map<Long, List<SubjectQuestionCountDTO>> insertCountsMap = new ConcurrentHashMap<>();
 
    public void updateCounts(Long userId,
                             List<SubjectQuestionCountDTO> deleteCounts,
                             List<SubjectQuestionCountDTO> insertCounts) {
        deleteCountsMap.put(userId, deleteCounts);
        insertCountsMap.put(userId, insertCounts);
    }
 
    public List<SubjectQuestionCountDTO> getDeleteCounts(Long userId) {
        return deleteCountsMap.getOrDefault(userId, Collections.emptyList());
    }
 
    public List<SubjectQuestionCountDTO> getInsertCounts(Long userId) {
        return insertCountsMap.getOrDefault(userId, Collections.emptyList());
    }
}
 