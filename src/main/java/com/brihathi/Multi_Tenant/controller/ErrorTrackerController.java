package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.dto.ErrorTrackerResponseDTO;
import com.brihathi.Multi_Tenant.service.ErrorTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read‑only API for the FE to fetch a user's outstanding errors.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ErrorTrackerController {
 
    private final ErrorTrackerService errorTrackerServiceService;
 
    /**
     * • `/api/error-tracker`           → all users (admin dashboards)  
     * • `/api/error-tracker?userId=15` → one user (student dashboard)
     */
    @GetMapping("/error-tracker")
    public ResponseEntity<List<ErrorTrackerResponseDTO>> getErrors(
            @RequestParam(value = "userId", required = false) Long userId) {
 
        List<ErrorTrackerResponseDTO> list = errorTrackerServiceService.getErrors(userId);
        return ResponseEntity.ok(list);
    }
}
 
 