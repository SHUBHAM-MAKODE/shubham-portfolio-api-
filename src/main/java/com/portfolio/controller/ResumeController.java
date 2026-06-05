package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Resume;
import com.portfolio.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * BACK-OFFICE INVENTORY FETCH: Retrieve all resume records in the database.
     * 🔓 PERMITTED: Open to your admin panel dashboard view grids for both roles.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Resume>>> fetchAll() {
        return resumeService.getAllResumes();
    }

    /**
     * COMMIT RESUME RECORD: Save a newly uploaded resume asset URL from Cloudinary.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.POST interceptor.
     * Only headers carrying a valid SUPER_ADMIN JWT token signature can execute this.
     */
    @PostMapping(value = "/upload")
    public ResponseEntity<ResponseStructure<Resume>> upload(
            @RequestParam("title") String title,
            @RequestParam("url") String url) {
        return resumeService.uploadResume(title, url);
    }

    /**
     * STATE MUTATION LINK: Flag a specific resume row as active and deactivate others.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.PATCH interceptor.
     */
    @PatchMapping("/set-active/{id}")
    public ResponseEntity<ResponseStructure<Resume>> activate(@PathVariable Long id) {
        return resumeService.setActiveResume(id);
    }

    /**
     * PURGE ASSET RECORD: Delete the resume tracking node out of your persistent database.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE interceptor.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> purge(@PathVariable Long id) {
        return resumeService.deleteResume(id);
    }

    /**
     * USER-SIDE DIRECT NODE VIEW: Fetch the single current active resume asset.
     * 🔓 PERMITTED: Fully open to public unauthenticated visitors via GET mappings.
     * This is also consumed instantly on boot by your User-Side Context Builder payload package!
     */
    @GetMapping("/active")
    public ResponseEntity<ResponseStructure<Resume>> fetchActiveResume() {
        return resumeService.getActiveResumeForUser();
    }
}