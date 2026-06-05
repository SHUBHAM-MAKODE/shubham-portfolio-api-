package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Experience;
import com.portfolio.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@CrossOrigin(origins = "*")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    /**
     * PUBLIC CHRONOLOGY FETCH: Get all experience tracking records.
     * 🔓 PERMITTED: Open to anonymous public visitors via GET mappings.
     * Also consumed by your Global Bootstrap Context to populate the client memory on load.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Experience>>> fetchAll() {
        return experienceService.getAllExperiences();
    }

    /**
     * INITIALIZE MILESTONE RECORD: Commit a new workspace tracking row with an optional logo.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.POST filter layer.
     * Only headers carrying a valid SUPER_ADMIN JWT token signature can execute this.
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Experience>> create(
            @RequestParam("company") String company,
            @RequestParam("role") String role,
            @RequestParam("duration") String duration,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam("descriptionPoints") List<String> descriptionPoints,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Experience exp = new Experience();
        exp.setCompany(company);
        exp.setRole(role);
        exp.setDuration(duration);
        exp.setLocation(location);
        exp.setDescriptionPoints(descriptionPoints);
        
        return experienceService.addExperience(exp, file);
    }

    /**
     * MUTATE ACCOUNT TRACK: Update parameters or replace an existing logo asset.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.PUT filter layer.
     */
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Experience>> update(
            @PathVariable int id,
            @RequestParam("company") String company,
            @RequestParam("role") String role,
            @RequestParam("duration") String duration,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam("descriptionPoints") List<String> descriptionPoints,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Experience details = new Experience();
        details.setCompany(company);
        details.setRole(role);
        details.setDuration(duration);
        details.setLocation(location);
        details.setDescriptionPoints(descriptionPoints);
        
        return experienceService.updateExperience(id, details, file);
    }

    /**
     * PURGE WORKSPACE TIMELINE: Delete record and associated Cloudinary assets from registry.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE filter layer.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> remove(@PathVariable int id) {
        return experienceService.deleteExperience(id);
    }
}