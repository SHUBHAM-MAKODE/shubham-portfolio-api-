package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Project;
import com.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * INITIALIZE PROJECT ASSET: Commit a new portfolio project card with an image layout canvas.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.POST filter layer.
     * Only requests backed by a valid SUPER_ADMIN signature token can execute this.
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Project>> saveProject(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("techStack") List<String> techStack, 
            @RequestParam(value = "liveUrl", required = false) String liveUrl,
            @RequestParam(value = "githubUrl", required = false) String githubUrl,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setTechStack(techStack);
        project.setLiveUrl(liveUrl);
        project.setGithubUrl(githubUrl);
        
        return projectService.addProject(project, file);
    }

    /**
     * PUBLIC SHOWCASE RETRIEVAL: Fetch all showcase project assets.
     * 🔓 PERMITTED: Open to anonymous public visitors via GET configurations.
     * This is also consumed by the frontend Context Provider to load your cards instantly on boot.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<Project>>> fetchAllProjects() {
        return projectService.getAllProjects();
    }

    /**
     * PURGE PROJECT MILESTONE: Delete record row and destroy its mapped image from Cloudinary storage.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE filter layer.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> removeProject(@PathVariable int id) {
        return projectService.deleteProject(id);
    }
}