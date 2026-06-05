package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Service;
import com.portfolio.media.MediaService;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceCardService {

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private AdminRepo adminRepo;
    
    @Autowired
    private MediaService mediaService;

    // 1. ADD SERVICE CARD
    public ResponseEntity<ResponseStructure<Service>> addService(Service service, MultipartFile imageFile) {
        ResponseStructure<Service> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            structure.setMessage("Admin account must be initialized first.");
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.PRECONDITION_FAILED);
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map<String, Object> uploadResult = mediaService.uploadFile(imageFile, "services");
                service.setImageUrl((String) uploadResult.get("secure_url"));
                service.setImagePublicId((String) uploadResult.get("public_id"));
            }

            service.setAdmin(admins.get(0));
            Service savedService = serviceRepo.save(service);

            structure.setStatus(HttpStatus.CREATED.value());
            structure.setMessage("Service card saved and asset uploaded successfully.");
            structure.setData(savedService);
            return new ResponseEntity<>(structure, HttpStatus.CREATED);

        } catch (IOException e) {
            structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            structure.setMessage("Failed to upload service image: " + e.getMessage());
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. GET ALL SERVICE CARDS
    public ResponseEntity<ResponseStructure<List<Service>>> getAllServices() {
        ResponseStructure<List<Service>> structure = new ResponseStructure<>();
        List<Service> services = serviceRepo.findAll();

        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Service offerings fetched successfully.");
        structure.setData(services);

        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    // 3. DELETE SERVICE CARD
    public ResponseEntity<ResponseStructure<String>> deleteService(int id) {
        ResponseStructure<String> structure = new ResponseStructure<>();

        if (!serviceRepo.existsById(id)) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Service with ID " + id + " does not exist.");
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        serviceRepo.deleteById(id);
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Service offering deleted successfully.");
        structure.setData("Deleted Service ID: " + id);

        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Service>> updateService(int id, Service details, MultipartFile file) {
        ResponseStructure<Service> structure = new ResponseStructure<>();
        
        // 2. Fetch the active record using a safe primitive-to-Long cast
        Optional<Service> optional = serviceRepo.findById((int) id);
        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Service card mapping with matching ID not found.");
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        Service existingService = optional.get();

        // 3. Sync incoming text changes from the dashboard form
        existingService.setTitle(details.getTitle());
        existingService.setDescription(details.getDescription());

        // 4. Binary Media Lifecycle Logic: Check if a new file is actually present
        if (file != null && !file.isEmpty()) {
            try {
                // A. Purge old asset from Cloudinary storage to prevent digital clutter
                if (existingService.getImagePublicId() != null && !existingService.getImagePublicId().isEmpty()) {
                    mediaService.deleteFile(existingService.getImagePublicId());
                }

                // B. Send fresh binary stream to your "services" subfolder in Cloudinary
                Map<String, Object> uploadResult = mediaService.uploadFile(file, "services");
                
                // C. Map the new Cloudinary coordinates back onto the entity model
                existingService.setImageUrl(uploadResult.get("secure_url").toString());
                existingService.setImagePublicId(uploadResult.get("public_id").toString());
                
            } catch (Exception e) {
                structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                structure.setMessage("Cloudinary asset swapping routine failed: " + e.getMessage());
                structure.setData(null);
                return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        // If file is null, Hibernate bypasses the image columns and keeps the old image intact!

        // 5. Commit mutations cleanly into your PostgreSQL table
        Service savedService = serviceRepo.save(existingService);

        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Service card entity committed and synchronized successfully.");
        structure.setData(savedService);

        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}