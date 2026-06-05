package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Education;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.EducationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EducationService {

    @Autowired
    private EducationRepo educationRepo;

    @Autowired
    private AdminRepo adminRepo;

    public ResponseEntity<ResponseStructure<List<Education>>> getAllEducation() {
        ResponseStructure<List<Education>> structure = new ResponseStructure<>();
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Academic credentials synchronized successfully.");
        structure.setData(educationRepo.findAll());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Education>> addEducation(Education edu) {
        ResponseStructure<Education> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            structure.setMessage("Administrative record missing.");
            return new ResponseEntity<>(structure, HttpStatus.PRECONDITION_FAILED);
        }

        edu.setAdmin(admins.get(0));
        structure.setStatus(HttpStatus.CREATED.value());
        structure.setData(educationRepo.save(edu));
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<Education>> updateEducation(int id, Education details) {
        ResponseStructure<Education> structure = new ResponseStructure<>();
        Optional<Education> optional = educationRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Academic record not found.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Education existing = optional.get();
        existing.setDegree(details.getDegree());
        existing.setInstitution(details.getInstitution());
        existing.setLocation(details.getLocation());
        existing.setDuration(details.getDuration());
        existing.setGrade(details.getGrade());
        existing.setDetails(details.getDetails());

        structure.setStatus(HttpStatus.OK.value());
        structure.setData(educationRepo.save(existing));
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteEducation(int id) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        if (!educationRepo.existsById(id)) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Record doesn't exist.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }
        educationRepo.deleteById(id);
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Academic timeline element dropped cleanly.");
        structure.setData("Dropped ID: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}