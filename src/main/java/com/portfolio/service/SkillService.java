package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Skill;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.SkillRepo;
import com.portfolio.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SkillService {

    @Autowired
    private SkillRepo skillRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private MediaService mediaService;

    public ResponseEntity<ResponseStructure<List<Skill>>> getAllSkills() {
        ResponseStructure<List<Skill>> structure = new ResponseStructure<>();
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Skills repository pulled successfully.");
        structure.setData(skillRepo.findAll());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Skill>> addSkill(Skill skill, MultipartFile file) {
        ResponseStructure<Skill> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            structure.setMessage("Admin anchor entity missing.");
            return new ResponseEntity<>(structure, HttpStatus.PRECONDITION_FAILED);
        }

        skill.setAdmin(admins.get(0));

        if (file != null && !file.isEmpty()) {
            try {
                Map<String, Object> res = mediaService.uploadFile(file, "skills");
                skill.setIconUrl(res.get("secure_url").toString());
                skill.setIconPublicId(res.get("public_id").toString());
            } catch (Exception e) {
                structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                structure.setMessage("Media error: " + e.getMessage());
                return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        structure.setStatus(HttpStatus.CREATED.value());
        structure.setData(skillRepo.save(skill));
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<Skill>> updateSkill(int id, String name, int rating, MultipartFile file) {
        ResponseStructure<Skill> structure = new ResponseStructure<>();
        Optional<Skill> optional = skillRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Skill entry not found.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Skill existing = optional.get();
        existing.setName(name);
        existing.setRating(rating);

        if (file != null && !file.isEmpty()) {
            try {
                if (existing.getIconPublicId() != null) {
                    mediaService.deleteFile(existing.getIconPublicId());
                }
                Map<String, Object> res = mediaService.uploadFile(file, "skills");
                existing.setIconUrl(res.get("secure_url").toString());
                existing.setIconPublicId(res.get("public_id").toString());
            } catch (Exception e) {
                structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                structure.setMessage("Media swapping failed.");
                return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        structure.setStatus(HttpStatus.OK.value());
        structure.setData(skillRepo.save(existing));
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteSkill(int id) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        Optional<Skill> optional = skillRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Skill profile element not found.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Skill skill = optional.get();
        try {
            if (skill.getIconPublicId() != null) {
                mediaService.deleteFile(skill.getIconPublicId());
            }
        } catch (Exception e) {
            System.err.println("Cloudinary cleanup failed: " + e.getMessage());
        }

        skillRepo.delete(skill);
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Skill entity popped from layout successfully.");
        structure.setData("Popped target: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}