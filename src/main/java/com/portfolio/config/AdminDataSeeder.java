package com.portfolio.config;

import com.portfolio.entity.Admin;
import com.portfolio.repo.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class AdminDataSeeder implements CommandLineRunner {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;
    
    @Value("${portfolio.app.admin.password}")
    private String adminPassword;
    
    @Value("${portfolio.app.guest.password}")
    private String guestPassword;

    @Override
    public void run(String... args) throws Exception {
        
        // ==========================================================================
        // 🔑 1. SEED MASTER SUPER ADMIN PROFILE
        // ==========================================================================
        Optional<Admin> existingSuperAdmin = adminRepo.findAll().stream()
                .filter(a -> "admin".equals(a.getUsername()))
                .findFirst();

        if (existingSuperAdmin.isEmpty()) {

            Admin superAdmin = new Admin();
            superAdmin.setUsername("admin"); // Master username credentials
            superAdmin.setName("Shubham Makode");
            
            
            if (passwordEncoder != null) {
                superAdmin.setPassword(passwordEncoder.encode(adminPassword));
            } else {
                superAdmin.setPassword(adminPassword);
            }
            
            // Assigning Super Admin Role access vectors
            superAdmin.setRole("ROLE_SUPER_ADMIN");

            adminRepo.save(superAdmin);
        }

        // ==========================================================================
        // 👁️ 2. SEED READ-ONLY RECRUITER GUEST PROFILE
        // ==========================================================================
        Optional<Admin> existingGuest = adminRepo.findAll().stream()
                .filter(a -> "guest".equals(a.getUsername()))
                .findFirst();

        if (existingGuest.isEmpty()) {

            Admin guestAdmin = new Admin();
            guestAdmin.setUsername("guest"); // Shared username for technical recruiters
            guestAdmin.setName("Recruiter Guest");
            
            // A simple, clean password for public previewing
            if (passwordEncoder != null) {
                guestAdmin.setPassword(passwordEncoder.encode(guestPassword));
            } else {
                guestAdmin.setPassword(guestPassword);
            }
            
            // 🔒 Assigning the Read-Only Viewer Role restriction vectors
            guestAdmin.setRole("ROLE_GUEST_VIEWER");

            adminRepo.save(guestAdmin);
        } else {
        }
    }
}