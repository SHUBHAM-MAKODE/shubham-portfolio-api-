package com.portfolio.config;

import com.portfolio.entity.Admin;
import com.portfolio.repo.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void run(String... args) throws Exception {
        
        // ==========================================================================
        // 🔑 1. SEED MASTER SUPER ADMIN PROFILE
        // ==========================================================================
        Optional<Admin> existingSuperAdmin = adminRepo.findAll().stream()
                .filter(a -> "admin".equals(a.getUsername()))
                .findFirst();

        if (existingSuperAdmin.isEmpty()) {
            System.out.println("⚡ Master administrator profile missing. Initializing Super Admin...");

            Admin superAdmin = new Admin();
            superAdmin.setUsername("admin"); // Master username credentials
            superAdmin.setName("Shubham Makode");
            
            String rawPassword = "6267494475";
            if (passwordEncoder != null) {
                superAdmin.setPassword(passwordEncoder.encode(rawPassword));
            } else {
                superAdmin.setPassword(rawPassword);
            }
            
            // Assigning Super Admin Role access vectors
            superAdmin.setRole("ROLE_SUPER_ADMIN");

            adminRepo.save(superAdmin);
            System.out.println("✅ Master Admin profile seeded: [Username: admin]");
        }

        // ==========================================================================
        // 👁️ 2. SEED READ-ONLY RECRUITER GUEST PROFILE
        // ==========================================================================
        Optional<Admin> existingGuest = adminRepo.findAll().stream()
                .filter(a -> "guest".equals(a.getUsername()))
                .findFirst();

        if (existingGuest.isEmpty()) {
            System.out.println("⚡ Recruiter preview credentials missing. Initializing Guest Admin...");

            Admin guestAdmin = new Admin();
            guestAdmin.setUsername("guest"); // Shared username for technical recruiters
            guestAdmin.setName("Recruiter Guest");
            
            // A simple, clean password for public previewing
            String rawGuestPassword = "guest123";
            if (passwordEncoder != null) {
                guestAdmin.setPassword(passwordEncoder.encode(rawGuestPassword));
            } else {
                guestAdmin.setPassword(rawGuestPassword);
            }
            
            // 🔒 Assigning the Read-Only Viewer Role restriction vectors
            guestAdmin.setRole("ROLE_GUEST_VIEWER");

            adminRepo.save(guestAdmin);
            System.out.println("✅ Recruiter Guest profile seeded: [Username: guest, Password: guest123]");
        } else {
            System.out.println("🌱 System account registries are fully initialized. Seeder step skipped.");
        }
    }
}