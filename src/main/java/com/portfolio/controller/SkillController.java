package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Skill;
import com.portfolio.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillController {

    @Autowired
    private SkillService skillService;

    /**
     * PUBLIC LOGO INVENTORY: Fetch all technology and tool skills.
     * 🔓 PERMITTED: Open to anonymous public visitors via GET mappings.
     * Consumed instantly on boot by your User-Side Context Provider to populate your tech stack section instantly.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Skill>>> fetchAll() {
        return skillService.getAllSkills();
    }

    /**
     * REGISTER NEW TECH STACK METRIC: Add a programming proficiency node with an icon file.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.POST filter layer.
     * Only headers carrying a valid SUPER_ADMIN JWT token signature can execute this.
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Skill>> save(
            @RequestParam("name") String name,
            @RequestParam("rating") int rating,
            @RequestParam("file") MultipartFile file) {
        
        Skill skill = new Skill();
        skill.setName(name);
        skill.setRating(rating);
        return skillService.addSkill(skill, file);
    }

    /**
     * MUTATE TECHNOLOGY NODE: Modify names, ratings, or replace a skill badge graphic asset.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.PUT filter layer.
     */
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Skill>> update(
            @PathVariable int id,
            @RequestParam("name") String name,
            @RequestParam("rating") int rating,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        return skillService.updateSkill(id, name, rating, file);
    }

    /**
     * PURGE TECH MILESTONE: Delete record and destroy its associated icon graphic from Cloudinary storage.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE filter layer.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> remove(@PathVariable int id) {
        return skillService.deleteSkill(id);
    }
}