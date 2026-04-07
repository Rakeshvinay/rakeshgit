package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.entity.Admins;
import java.util.List;
import java.util.Optional;

public interface AdminService {
    Admins createAdmin(Admins admin);
    List<Admins> getAllAdmins();
    Optional<Admins> getAdminById(Integer id);
    Optional<Admins> getAdminByEmail(String email);
    Admins updateAdmin(Admins admin);
    void deleteAdmin(Integer id);
    boolean validateAdminCredentials(String email, String password);

} 
