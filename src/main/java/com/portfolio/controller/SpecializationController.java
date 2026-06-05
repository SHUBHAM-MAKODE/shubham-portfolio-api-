package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Specialization;
import com.portfolio.service.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@CrossOrigin(origins = "*")
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

    /**
     * PUBLIC DOMAIN SHOWCASE: Retrieve all specialization cards.
     * 🔓 PERMITTED: Fully open to public anonymous visitors via global GET configurations.
     * This is also bundled into your single User-Side Portfolio Hub call to maximize platform performance.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Specialization>>> fetchAll() {
        return specializationService.getAllSpecializations();
    }

    /**
     * REGISTER NEW AREA OF EXPERTISE: Add a specialization metric with an icon image file.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.POST filter rules.
     * Only headers carrying a valid SUPER_ADMIN JWT token signature can pass through this path.
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Specialization>> create(
            @RequestParam("title") String title,
            @RequestParam("proficiency") String proficiency,
            @RequestParam("details") List<String> details,
            @RequestParam("file") MultipartFile file) {
        
        Specialization spec = new Specialization();
        spec.setTitle(title);
        spec.setProficiency(proficiency);
        spec.setDetails(details);
        return specializationService.addSpecialization(spec, file);
    }

    /**
     * MUTATE DOMAIN METRIC: Modify focus areas, proficiency tiers, or replace the Cloudinary graphic asset.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.PUT filter rules.
     */
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Specialization>> update(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("proficiency") String proficiency,
            @RequestParam("details") List<String> details,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Specialization spec = new Specialization();
        spec.setTitle(title);
        spec.setProficiency(proficiency);
        spec.setDetails(details);
        return specializationService.updateSpecialization(id, spec, file);
    }

    /**
     * PURGE EXPERTISE TRACK: Delete the record row and remove its corresponding graphic from Cloudinary storage.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE filter rules.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> remove(@PathVariable Long id) {
        return specializationService.deleteSpecialization(id);
    }
}