package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Education;
import com.portfolio.service.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/education")
@CrossOrigin(origins = "*")
public class EducationController {

    @Autowired
    private EducationService educationService;

    /**
     * PUBLIC INVENTORY RETRIEVAL: Fetch all education records.
     * 🔓 PERMITTED: Open to public anonymous visitors via GET permitAll() configs.
     * It is also consumed by the Admin Dashboard to fill data tables for both roles.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Education>>> fetchAll() {
        return educationService.getAllEducation();
    }

    /**
     * CREATE NEW MILESTONE: Add an academic credential record.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.POST interceptor.
     * Requires headers with a valid SUPER_ADMIN JWT signature.
     */
    @PostMapping("/add")
    public ResponseEntity<ResponseStructure<Education>> create(@RequestBody Education edu) {
        return educationService.addEducation(edu);
    }

    /**
     * MUTATE ACCOUNT NODE: Update an existing academic milestone entry.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.PUT interceptor.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseStructure<Education>> update(@PathVariable int id, @RequestBody Education edu) {
        return educationService.updateEducation(id, edu);
    }

    /**
     * PURGE ACADEMIC NODE: Delete an education row from the database registry.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE interceptor.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> remove(@PathVariable int id) {
        return educationService.deleteEducation(id);
    }
}