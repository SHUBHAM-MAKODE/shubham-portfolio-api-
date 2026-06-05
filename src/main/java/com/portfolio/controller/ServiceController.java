package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Service;
import com.portfolio.service.ServiceCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {

    @Autowired
    private ServiceCardService serviceCardService;

    /**
     * INITIALIZE SERVICE NODE: Add a new service offering card with an optional icon graphic file.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.POST interceptor.
     * Only requests backed by a valid SUPER_ADMIN signature token can execute this.
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Service>> saveService(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Service service = new Service();
        service.setTitle(title);
        service.setDescription(description);
        
        return serviceCardService.addService(service, file);
    }

    /**
     * PUBLIC OFFERING FETCH: Retrieve all service matrix metadata cards.
     * 🔓 PERMITTED: Open to anonymous public visitors via GET configurations.
     * This is also grabbed instantly on boot by your User-Side Context Provider to minimize server load.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Service>>> fetchAllServices() {
        return serviceCardService.getAllServices();
    }

    /**
     * MUTATE SERVICE PARAMETERS: Update textual definitions or replace an icon image asset.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.PUT interceptor.
     */
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Service>> updateService(
            @PathVariable int id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Service details = new Service();
        details.setTitle(title);
        details.setDescription(description);
        
        return serviceCardService.updateService(id, details, file);
    }

    /**
     * PURGE SERVICE METRIC: Delete the record row and destroy its mapped image from Cloudinary storage.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE interceptor.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> removeService(@PathVariable int id) {
        return serviceCardService.deleteService(id);
    }
}