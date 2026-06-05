package com.portfolio.controller;

import com.portfolio.dto.AuthRequest;
import com.portfolio.dto.PublicProfileDto;
import com.portfolio.service.AdminService;
import com.portfolio.dto.ResponseStructure;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    /**
     * Fetch the baseline information to render the public portfolio homepage.
     * 🔓 PERMITTED: Open to all visitors via SecurityConfig matchers.
     */
    @GetMapping("/profile")
    public ResponseEntity<ResponseStructure<PublicProfileDto>> getPublicProfile() {
        return adminService.getPublicProfileData();
    }
    
    /**
     * Process authentication credentials for both Master Admin and Guest Recruiter.
     * 🔓 PERMITTED: Open to unauthenticated clients to exchange credentials for a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<String>> login(@RequestBody AuthRequest authRequest) {
        return adminService.loginAdmin(authRequest);
    }
    
    /**
     * Mutate professional background tags/roles.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER at the filter perimeter.
     * Only headers bearing a valid SUPER_ADMIN JWT signature can execute this.
     */
    @PutMapping("/update-roles")
    public ResponseEntity<ResponseStructure<PublicProfileDto>> updateProfessionalRoles(
            @RequestBody List<String> updatedRoles) {
        return adminService.updateAdminRoles(updatedRoles);
    }
}