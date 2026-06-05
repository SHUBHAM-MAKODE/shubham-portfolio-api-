package com.portfolio.service;

import com.portfolio.dto.PublicProfileDto;
import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.repo.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.portfolio.security.JwtUtil;

@Service
public class AdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<ResponseStructure<Admin>> createRootAdmin(Admin admin) {
        ResponseStructure<Admin> structure = new ResponseStructure<>();
        List<Admin> existingAdmins = adminRepo.findAll();

        if (!existingAdmins.isEmpty()) {
            structure.setStatus(HttpStatus.BAD_REQUEST.value());
            structure.setMessage("Root admin account already exists.");
            structure.setData(existingAdmins.get(0));
            return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
        }

        // Hash your raw password before writing to the database
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Admin savedAdmin = adminRepo.save(admin);

        structure.setStatus(HttpStatus.CREATED.value());
        structure.setMessage("Root admin profile initialized with secure hashing.");
        structure.setData(savedAdmin);

        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }
    
    @Autowired
    private JwtUtil jwtUtil;

//    public ResponseEntity<ResponseStructure<String>> loginAdmin(com.portfolio.dto.AuthRequest authRequest) {
//        ResponseStructure<String> structure = new ResponseStructure<>();
//        List<Admin> admins = adminRepo.findAll();
//
//        if (admins.isEmpty()) {
//            structure.setStatus(HttpStatus.NOT_FOUND.value());
//            structure.setMessage("No admin profile found. Please run system setup first.");
//            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
//        }
//
//        Admin admin = admins.get(0);
//
//        // Verify if incoming raw text matches your saved BCrypt hash
//        if (admin.getUsername().equals(authRequest.getUsername()) && 
//            passwordEncoder.matches(authRequest.getPassword(), admin.getPassword())) {
//            
//            // Generate token linked to your username
//            String generatedToken = jwtUtil.generateToken(admin.getUsername());
//
//            structure.setStatus(HttpStatus.OK.value());
//            structure.setMessage("Authentication successful! Copy your bearer token.");
//            structure.setData(generatedToken); // This sends the JWT back to your frontend/Postman
//            return new ResponseEntity<>(structure, HttpStatus.OK);
//        }
//
//        structure.setStatus(HttpStatus.UNAUTHORIZED.value());
//        structure.setMessage("Invalid username or password configuration.");
//        structure.setData(null);
//        return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
//    }
    
    public ResponseEntity<ResponseStructure<String>> loginAdmin(com.portfolio.dto.AuthRequest authRequest) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("No admin profile found. Please run system setup first.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Admin admin = admins.get(0);

        // Verify if incoming raw text matches your saved BCrypt hash
        if (admin.getUsername().equals(authRequest.getUsername()) && 
            passwordEncoder.matches(authRequest.getPassword(), admin.getPassword())) {
            
            // 🌟 FIXED: Pass BOTH the username AND the role string (e.g., "ROLE_SUPER_ADMIN")
            String generatedToken = jwtUtil.generateToken(admin.getUsername(), admin.getRole());

            structure.setStatus(HttpStatus.OK.value());
            structure.setMessage("Authentication successful! Copy your bearer token.");
            structure.setData(generatedToken); // This sends the enriched JWT back to your frontend
            return new ResponseEntity<>(structure, HttpStatus.OK);
        }

        structure.setStatus(HttpStatus.UNAUTHORIZED.value());
        structure.setMessage("Invalid username or password configuration.");
        structure.setData(null);
        return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
    }
    
 // FIXED: Changed entity payload wrapping to our secure PublicProfileDto
    public ResponseEntity<ResponseStructure<PublicProfileDto>> getPublicProfileData() {
        ResponseStructure<PublicProfileDto> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("No administrator account has been initialized yet.");
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Admin rootAdmin = admins.get(0);

        // Map ONLY the safe, public parameters directly into the DTO construct
        PublicProfileDto publicProfile = new PublicProfileDto(
            rootAdmin.getName(),
            rootAdmin.getRoles() // Your dynamic list of hero typed descriptions
        );

        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Public profile parameters synchronized securely.");
        structure.setData(publicProfile); // Sending out only the safe subset object

        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<PublicProfileDto>> updateAdminRoles(List<String> updatedRoles) {
        ResponseStructure<PublicProfileDto> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Administrator account not initialized.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Admin rootAdmin = admins.get(0);
        
        // FIXED: Swapped out .toList() for a mutable ArrayList collector
        List<String> cleanedRoles = updatedRoles.stream()
                .filter(role -> role != null && !role.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList()); // Now it's a completely open ArrayList!

        // Overwrite the element collection list safely
        rootAdmin.setRoles(cleanedRoles);
        adminRepo.save(rootAdmin);

        // Map cleanly back to your secure public DTO layout
        PublicProfileDto updatedDto = new PublicProfileDto(rootAdmin.getName(), rootAdmin.getRoles());

        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Hero section designations updated successfully across core schema.");
        structure.setData(updatedDto);

        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}