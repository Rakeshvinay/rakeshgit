package com.brihathi.Multi_Tenant.controller;

import com.brihathi.Multi_Tenant.entity.Admins;
import com.brihathi.Multi_Tenant.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestBody Admins admin) {
        Admins createdAdmin = adminService.createAdmin(admin);
        return ResponseEntity.ok(Map.of(
            "message", "Admin created successfully",
            "admin", createdAdmin
        ));
    }

    @GetMapping
    public ResponseEntity<List<Admins>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Integer id) {
        return adminService.getAdminById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAdminByEmail(@PathVariable String email) {
        return adminService.getAdminByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody Admins admin) {
        admin.setAdminId(id);
        Admins updatedAdmin = adminService.updateAdmin(admin);
        return ResponseEntity.ok(Map.of(
            "message", "Admin updated successfully",
            "admin", updatedAdmin
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok(Map.of("message", "Admin deleted successfully"));
    }
} 