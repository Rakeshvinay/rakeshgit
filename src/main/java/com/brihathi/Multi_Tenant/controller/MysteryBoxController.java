package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.dto.MysteryBoxResponseDTO;
import com.brihathi.Multi_Tenant.dto.SubjectQuestionCountDTO;
import com.brihathi.Multi_Tenant.service.MysteryBoxDataHolder;
import com.brihathi.Multi_Tenant.service.MysteryBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mystery-box")
@RequiredArgsConstructor
public class MysteryBoxController {

    private final MysteryBoxService mysteryBoxService;

    private final MysteryBoxDataHolder mysteryBoxDataHolder;

    /**
     * Get stored mystery box questions for a user and exam
     */


     @GetMapping("/{userId}")
public ResponseEntity<List<MysteryBoxResponseDTO>> getMysteryBoxQuestions(@PathVariable Long userId) {
    List<MysteryBoxResponseDTO> questions = mysteryBoxService.getMysteryBoxQuestions(userId);
    return ResponseEntity.ok(questions);

}

    @GetMapping("/{userId}/deleted-count")
    public List<SubjectQuestionCountDTO> getDeleteCounts(@PathVariable Long userId) {
        return mysteryBoxDataHolder.getDeleteCounts(userId);
    }

    @GetMapping("/{userId}/inserted-count")
    public List<SubjectQuestionCountDTO> getInsertCounts(@PathVariable Long userId) {
        return mysteryBoxDataHolder.getInsertCounts(userId);
    }
}
