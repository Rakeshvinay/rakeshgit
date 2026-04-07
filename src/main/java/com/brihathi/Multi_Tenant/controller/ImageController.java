package com.brihathi.Multi_Tenant.controller;
 
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.nio.file.Path;
import java.nio.file.Paths;
 
@RestController
@RequestMapping("/api/images")
public class ImageController {
 
    private static final String IMAGE_DIRECTORY = "C:/opt/swan/profile-pictures/";
 
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(IMAGE_DIRECTORY).resolve(filename).normalize();
            Resource resource = new UrlResource(imagePath.toUri());
 
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
 
            MediaType contentType = filename.toLowerCase().endsWith(".png")
                    ? MediaType.IMAGE_PNG
                    : MediaType.IMAGE_JPEG;
 
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
 
 