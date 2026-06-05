package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Project;
import com.portfolio.media.MediaService;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

	@Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private MediaService mediaService;

    public ResponseEntity<ResponseStructure<Project>> addProject(Project project, MultipartFile imageFile) {
        ResponseStructure<Project> structure = new ResponseStructure<>();
        List<Admin> admins = adminRepo.findAll();

        if (admins.isEmpty()) {
            structure.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            structure.setMessage("Admin account must be initialized first.");
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.PRECONDITION_FAILED);
        }

        try {
            // 1. If a screenshot file is provided, stream it to Cloudinary
            if (imageFile != null && !imageFile.isEmpty()) {
                // Uploads directly to the "portfolio/projects" directory bucket
                Map<String, Object> uploadResult = mediaService.uploadFile(imageFile, "projects");
                
                project.setImageUrl((String) uploadResult.get("secure_url"));
                project.setImagePublicId((String) uploadResult.get("public_id"));
            }

            // 2. Pair it with the portfolio anchor profile
            project.setAdmin(admins.get(0));
            
            // 3. Persist core text and collection mappings to database tables
            Project savedProject = projectRepo.save(project);

            structure.setStatus(HttpStatus.CREATED.value());
            structure.setMessage("Project saved and asset media uploaded successfully.");
            structure.setData(savedProject);
            return new ResponseEntity<>(structure, HttpStatus.CREATED);

        } catch (IOException e) {
            structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            structure.setMessage("Failed to upload project screenshot image: " + e.getMessage());
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 2. GET ALL PROJECTS
    public ResponseEntity<ResponseStructure<List<Project>>> getAllProjects() {
        ResponseStructure<List<Project>> structure = new ResponseStructure<>();
        List<Project> projects = projectRepo.findAll();

        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Projects fetched successfully.");
        structure.setData(projects);

        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    // 3. DELETE PROJECT
    public ResponseEntity<ResponseStructure<String>> deleteProject(int id) {
        ResponseStructure<String> structure = new ResponseStructure<>();

        if (!projectRepo.existsById(id)) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Project with ID " + id + " does not exist.");
            structure.setData(null);
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        projectRepo.deleteById(id);
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Project deleted successfully.");
        structure.setData("Deleted Project ID: " + id);

        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}