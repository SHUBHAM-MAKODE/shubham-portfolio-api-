package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Resume;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.ResumeRepo;
import com.portfolio.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepo resumeRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private MediaService mediaService;

    public ResponseEntity<ResponseStructure<List<Resume>>> getAllResumes() {
        ResponseStructure<List<Resume>> structure = new ResponseStructure<>();
        structure.setStatus(HttpStatus.OK.value());
        structure.setData(resumeRepo.findAll());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

   
    public ResponseEntity<ResponseStructure<Resume>> uploadResume(String title, String url) {
    	ResponseStructure<Resume> structure = new ResponseStructure<>();
    	List<Admin> admins = adminRepo.findAll();
    	
    	if (admins.isEmpty()) {
    		structure.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    		structure.setMessage("Admin anchor entity missing.");
    		return new ResponseEntity<>(structure, HttpStatus.PRECONDITION_FAILED);
    	}
    	
    	if (url == null ) {
    		structure.setStatus(HttpStatus.BAD_REQUEST.value());
    		structure.setMessage("Document transmission stream cannot be empty.");
    		return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
    	}
    	
    	try {
    		
    		Resume resume = new Resume();
    		resume.setTitle(title);
    		resume.setDownloadUrl(url);
    		resume.setPublicId(url);
    		resume.setAdmin(admins.get(0));
    		
    		if (resumeRepo.count() == 0) {
    			resume.setActive(true);
    		} else {
    			resume.setActive(false);
    		}
    		
    		structure.setStatus(HttpStatus.CREATED.value());
    		structure.setData(resumeRepo.save(resume));
    		return new ResponseEntity<>(structure, HttpStatus.CREATED);
    	} catch (Exception e) {
    		structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    		structure.setMessage("Document pipeline failed: " + e.getMessage());
    		return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

    public ResponseEntity<ResponseStructure<Resume>> setActiveResume(Long id) {
        ResponseStructure<Resume> structure = new ResponseStructure<>();
        Optional<Resume> optional = resumeRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        resumeRepo.clearActiveFlags();
        
        Resume target = optional.get();
        target.setActive(true);
        
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Primary resume assignment updated successfully.");
        structure.setData(resumeRepo.save(target));
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteResume(Long id) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        Optional<Resume> optional = resumeRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Resume current = optional.get();
        
        // 🌟 FIXED: Standardized to Boolean.TRUE.equals() for seamless consistency across handlers
        if (Boolean.TRUE.equals(current.isActive())) {
            structure.setStatus(HttpStatus.BAD_REQUEST.value());
            structure.setMessage("Cannot drop an active document node. Set another entry as active first.");
            return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
        }

        try {
            mediaService.deleteFile(current.getPublicId());
        } catch (Exception e) {
            System.err.println("Cloudinary cleanup warning: " + e.getMessage());
        }

        resumeRepo.delete(current);
        structure.setStatus(HttpStatus.OK.value());
        structure.setData("Popped Core ID: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

//    public ResponseEntity<ResponseStructure<Resume>> getActiveResumeForUser() {
//        ResponseStructure<Resume> structure = new ResponseStructure<>();
//        List<Resume> allResumes = resumeRepo.findAll();
//        
//        // 🌟 EXCELLENT: Clean, null-safe stream pattern using Boolean utility wrapper
//        Optional<Resume> activeResume = allResumes.stream()
//                .filter(r -> Boolean.TRUE.equals(r.isActive()))
//                .findFirst();
//
//        if (activeResume.isEmpty()) {
//            structure.setStatus(HttpStatus.NOT_FOUND.value());
//            structure.setMessage("No active production resume asset currently flagged in database.");
//            structure.setData(null);
//            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
//        }
//
//        structure.setStatus(HttpStatus.OK.value());
//        structure.setMessage("Active resume layout configuration retrieved successfully.");
//        structure.setData(activeResume.get());
//        return new ResponseEntity<>(structure, HttpStatus.OK);
//    }
    public ResponseEntity<ResponseStructure<Resume>> getActiveResumeForUser() {
        ResponseStructure<Resume> structure = new ResponseStructure<>();
        
        // 🌟 OPTIMIZED: The database now processes the filter instantly
        Optional<Resume> activeResume = resumeRepo.findByIsActiveTrue();

        if (activeResume.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("No active production resume asset currently flagged in database.");
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Active resume layout configuration retrieved successfully.");
        structure.setData(activeResume.get());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}