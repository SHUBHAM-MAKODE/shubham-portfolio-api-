package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Experience;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.ExperienceRepo;
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
public class ExperienceService {

    @Autowired
    private ExperienceRepo experienceRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private MediaService mediaService;

    // A. READ PIPELINE
    public ResponseEntity<ResponseStructure<List<Experience>>> getAllExperiences() {
        ResponseStructure<List<Experience>> structure = new ResponseStructure<>();
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Career chronology stream synchronized successfully.");
        structure.setData(experienceRepo.findAll());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    // B. WRITE PIPELINE (WITH MEDIA BINDING)
    public ResponseEntity<ResponseStructure<Experience>> addExperience(Experience experience, MultipartFile file) {
        ResponseStructure<Experience> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            structure.setMessage("Administrative record missing. Timeline deployment aborted.");
            return new ResponseEntity<>(structure, HttpStatus.PRECONDITION_FAILED);
        }

        // Handle structural constraint rules by binding back-reference context
        experience.setAdmin(admins.get(0));

        // Upload company branding asset to portfolio/logos directory in Cloudinary
        if (file != null && !file.isEmpty()) {
            try {
                Map<String, Object> uploadResult = mediaService.uploadFile(file, "logos");
                experience.setCompanyLogoUrl(uploadResult.get("secure_url").toString());
                experience.setCompanyLogoPublicId(uploadResult.get("public_id").toString());
            } catch (Exception e) {
                structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                structure.setMessage("Failed to pipe media asset to Cloudinary: " + e.getMessage());
                return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        structure.setStatus(HttpStatus.CREATED.value());
        structure.setMessage("Placement record successfully saved.");
        structure.setData(experienceRepo.save(experience));
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    // C. MUTATION UPDATE PIPELINE
    public ResponseEntity<ResponseStructure<Experience>> updateExperience(int id, Experience details, MultipartFile file) {
        ResponseStructure<Experience> structure = new ResponseStructure<>();
        Optional<Experience> optional = experienceRepo.findById((int) id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Experience entry targeted for modification not found.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Experience existing = optional.get();
        existing.setCompany(details.getCompany());
        existing.setRole(details.getRole());
        existing.setDuration(details.getDuration());
        existing.setLocation(details.getLocation());
        existing.setDescriptionPoints(details.getDescriptionPoints());

        // File lifecycle swap logic
        if (file != null && !file.isEmpty()) {
            try {
                // Destroy old branding asset if it exists to preserve account limits
                if (existing.getCompanyLogoPublicId() != null && !existing.getCompanyLogoPublicId().isEmpty()) {
                    mediaService.deleteFile(existing.getCompanyLogoPublicId());
                }
                
                Map<String, Object> uploadResult = mediaService.uploadFile(file, "logos");
                existing.setCompanyLogoUrl(uploadResult.get("secure_url").toString());
                existing.setCompanyLogoPublicId(uploadResult.get("public_id").toString());
            } catch (Exception e) {
                structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                structure.setMessage("Media storage modification aborted: " + e.getMessage());
                return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Timeline properties committed cleanly.");
        structure.setData(experienceRepo.save(existing));
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    // D. PURGE PIPELINE
    public ResponseEntity<ResponseStructure<String>> deleteExperience(int id) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        Optional<Experience> optional = experienceRepo.findById((int) id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Record does not exist.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Experience existing = optional.get();
        
        // Clean cloud asset storage signature before dropping row data
        try {
            if (existing.getCompanyLogoPublicId() != null && !existing.getCompanyLogoPublicId().isEmpty()) {
                mediaService.deleteFile(existing.getCompanyLogoPublicId());
            }
        } catch (Exception e) {
            System.err.println("Non-blocking warning: Cloudinary file clean failed: " + e.getMessage());
        }

        experienceRepo.delete(existing);
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Timeline node removed and child data collections cleanly dropped.");
        structure.setData("Cleared Entry Key: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}