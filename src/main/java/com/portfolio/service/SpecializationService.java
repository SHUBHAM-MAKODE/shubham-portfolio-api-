package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Specialization;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.SpecializationRepo;
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
public class SpecializationService {

    @Autowired
    private SpecializationRepo specializationRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private MediaService mediaService;

    public ResponseEntity<ResponseStructure<List<Specialization>>> getAllSpecializations() {
        ResponseStructure<List<Specialization>> structure = new ResponseStructure<>();
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Specializations pulled successfully.");
        structure.setData(specializationRepo.findAll());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Specialization>> addSpecialization(Specialization spec, MultipartFile file) {
        ResponseStructure<Specialization> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            structure.setMessage("Admin anchor profile missing.");
            return new ResponseEntity<>(structure, HttpStatus.PRECONDITION_FAILED);
        }
        spec.setAdmin(admins.get(0));

        if (file != null && !file.isEmpty()) {
            try {
                Map<String, Object> res = mediaService.uploadFile(file, "specializations");
                spec.setLogoUrl(res.get("secure_url").toString());
                spec.setLogoPublicId(res.get("public_id").toString());
            } catch (Exception e) {
                structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                structure.setMessage("Cloudinary upload failed: " + e.getMessage());
                return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        structure.setStatus(HttpStatus.CREATED.value());
        structure.setData(specializationRepo.save(spec));
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<Specialization>> updateSpecialization(Long id, Specialization details, MultipartFile file) {
        ResponseStructure<Specialization> structure = new ResponseStructure<>();
        Optional<Specialization> optional = specializationRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Specialization not found.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Specialization existing = optional.get();
        existing.setTitle(details.getTitle());
        existing.setProficiency(details.getProficiency());
        existing.setDetails(details.getDetails());

        if (file != null && !file.isEmpty()) {
            try {
                if (existing.getLogoPublicId() != null) {
                    mediaService.deleteFile(existing.getLogoPublicId());
                }
                Map<String, Object> res = mediaService.uploadFile(file, "specializations");
                existing.setLogoUrl(res.get("secure_url").toString());
                existing.setLogoPublicId(res.get("public_id").toString());
            } catch (Exception e) {
                structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                structure.setMessage("Media swapping failed.");
                return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        structure.setStatus(HttpStatus.OK.value());
        structure.setData(specializationRepo.save(existing));
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteSpecialization(Long id) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        Optional<Specialization> optional = specializationRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Specialization spec = optional.get();
        try {
            if (spec.getLogoPublicId() != null) mediaService.deleteFile(spec.getLogoPublicId());
        } catch (Exception e) {
            System.err.println("Asset cleanup fallback warning: " + e.getMessage());
        }

        specializationRepo.delete(spec);
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Specialization popped cleanly.");
        structure.setData("Popped Key: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}