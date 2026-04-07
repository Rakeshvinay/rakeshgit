package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.service.OverallSwotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import com.brihathi.Multi_Tenant.dto.SwotResponseDTO;
import com.brihathi.Multi_Tenant.dto.SwotNormalResponseDTO;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OverallSwotController {

    private final OverallSwotService overallSwotService;

    @GetMapping("/{userId}/overall-swot")
    public ResponseEntity<?> getOverallSwot(@PathVariable Long userId) {
        try {
            SwotNormalResponseDTO result = overallSwotService.generateOverallSwot(userId);
            if (result == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                    "message", "No SWOT data found for user " + userId
                ));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to generate overall SWOT",
                "message", e.getMessage()
            ));
        }
    }
}
