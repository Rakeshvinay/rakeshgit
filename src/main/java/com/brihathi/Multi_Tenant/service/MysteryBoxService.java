package com.brihathi.Multi_Tenant.service;
 
import com.brihathi.Multi_Tenant.dto.MysteryBoxResponseDTO;
import com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO;
 
import java.util.List;
 
public interface MysteryBoxService {
 
    /**
     * Process and store unattempted questions into mystery_box table
     *
     * @param userId the user ID
     * @param examId the exam ID
     */
    void processMysteryBox(Long userId, Long examId);
 
    /**
     * Fetch unattempted questions for the mystery box
     *
     * @param userId the user ID
     * @param examId the exam ID
     * @return list of MysteryBoxResponseDTO
     */
    //List<MysteryBoxResponseDTO> getMysteryBoxQuestions(Long userId, Long examId);
    //List<MysteryBoxResponseDTO> getMysteryBoxQuestions(Long userId);
    List<MysteryBoxResponseDTO> getMysteryBoxQuestions(Long userId);
 
    List<SubjectQuestionCountDTO> getDeleteCounts(Long userId, Long examId);
    List<SubjectQuestionCountDTO> getInsertCounts(Long userId, Long examId);
}
 