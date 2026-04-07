

package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.entity.Chapter;
import com.brihathi.Multi_Tenant.service.ChapterService;
import com.brihathi.Multi_Tenant.dto.ChapterUploadResponse;
import com.brihathi.Multi_Tenant.dto.ChapterCreateResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is required"));
        }

        // Extract subdomain (brihathi from brihathi.localhost:8080)
        String host = request.getHeader("Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }
        String subdomain = extractSubdomain(host);

        var chapters = chapterService.uploadChaptersFromExcel(file, subdomain);
        ChapterUploadResponse response =
                new ChapterUploadResponse("Chapters uploaded successfully", chapters);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-all-chapter")
    public ResponseEntity<ChapterCreateResponse> createAllChapter() {
        Chapter savedChapter = chapterService.createAllChapter();
        ChapterCreateResponse response =
                new ChapterCreateResponse("ALL chapter created successfully", savedChapter);
        return ResponseEntity.ok(response);
    }

    private String extractSubdomain(String host) {
        String h = host.split(":")[0];
        String[] parts = h.split("\\.");
        return parts.length > 1 ? parts[0] : h;
    }
}
