package com.brihathi.Multi_Tenant.serviceimpl;
 
import com.brihathi.Multi_Tenant.entity.Admins;
import com.brihathi.Multi_Tenant.repository.AdminRepository;
import com.brihathi.Multi_Tenant.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.Optional;
 
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
 
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
 
    @Override
    @Transactional
    public Admins createAdmin(Admins admin) {
        // Validate required fields
        if (admin.getName() == null || admin.getName().trim().isEmpty()) {
            throw new RuntimeException("Admin name is required");
        }
        if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Admin email is required");
        }
        if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Admin password is required");
        }
        // Check if admin with same email already exists
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Admin with email " + admin.getEmail() + " already exists");
        }
        // Encode password using bcrypt
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        // Set default role if not provided
        if (admin.getRole() == null || admin.getRole().isEmpty()) {
            admin.setRole("ADMIN");
        }
        // Set default status if not provided
        if (admin.getStatus() == null || admin.getStatus().isEmpty()) {
            admin.setStatus("ACTIVE");
        }
        return adminRepository.save(admin);
    }
 
    @Override
    public List<Admins> getAllAdmins() {
        return adminRepository.findAll();
    }
 
    @Override
    public Optional<Admins> getAdminById(Integer id) {
        return adminRepository.findById(id);
    }
 
    @Override
    public Optional<Admins> getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
 
    @Override
    @Transactional
    public Admins updateAdmin(Admins admin) {
        Admins existingAdmin = adminRepository.findById(admin.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + admin.getAdminId()));
        // Validate required fields
        if (admin.getName() == null || admin.getName().trim().isEmpty()) {
            throw new RuntimeException("Admin name is required");
        }
        if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Admin email is required");
        }
        // Check if email is being updated to one that already exists for another admin
        Optional<Admins> adminWithEmail = adminRepository.findByEmail(admin.getEmail());
        if (adminWithEmail.isPresent() && !adminWithEmail.get().getAdminId().equals(admin.getAdminId())) {
            throw new RuntimeException("Another admin with email " + admin.getEmail() + " already exists");
        }
        // Update fields
        existingAdmin.setName(admin.getName());
        existingAdmin.setEmail(admin.getEmail());
        existingAdmin.setRole(admin.getRole());
        existingAdmin.setStatus(admin.getStatus());
        // Only update password if a new one is provided
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            existingAdmin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        return adminRepository.save(existingAdmin);
    }
 
    @Override
    @Transactional
    public void deleteAdmin(Integer id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
    }
    @Override
    public boolean validateAdminCredentials(String email, String password) {
        try {
            Admins admin = adminRepository.findByEmail(email)
                    .orElse(null);
           
            if (admin == null) {
                return false;
            }
           
            // Check if admin is active
            if (!"ACTIVE".equals(admin.getStatus())) {
                return false;
            }
           
            // Verify password
            return passwordEncoder.matches(password, admin.getPassword());
           
        } catch (Exception e) {
            return false;
        }
    }
}
