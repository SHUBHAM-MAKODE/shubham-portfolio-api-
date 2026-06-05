package com.portfolio.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.Admin;
import com.portfolio.entity.Education;
import com.portfolio.entity.Experience;
import com.portfolio.entity.Project;
import com.portfolio.entity.Resume;
import com.portfolio.entity.Service;
import com.portfolio.entity.Skill;
import com.portfolio.repo.AdminRepo;
import com.portfolio.repo.EducationRepo;
import com.portfolio.repo.ExperienceRepo;
import com.portfolio.repo.ProjectRepo;
import com.portfolio.repo.ResumeRepo;
import com.portfolio.repo.ServiceRepo;
import com.portfolio.repo.SkillRepo;

@RestController
@RequestMapping("/api/trial")
public class trail {
	
	@Autowired
	AdminRepo adminRepo;
	
	@Autowired
	EducationRepo eduRepo;
	
	@Autowired
	ExperienceRepo expRepo;
	
	
	@Autowired
	ProjectRepo projectRepo;
	
	@Autowired
	ResumeRepo resumeRepo;
	
	
	@Autowired
	ServiceRepo serviceRepo;
	
	@Autowired
	SkillRepo skillRepo;
	
	@GetMapping("/getAll")
	public ResponseEntity<ResponseStructure<Map<String, Object>>> getAllSuperAdminData() {
	    
	    // 1. Fetch only the admins who hold the "SUPER_ADMIN" role
	    List<Admin> superAdmins = adminRepo.findByRole("ROLE_SUPER_ADMIN");
	    
	    Admin admin = superAdmins.get(0);
	    // 2. Initialize collections to aggregate data across all Super Admins
	    List<Education> superAdminEducation = new java.util.ArrayList<>();
	    List<Experience> superAdminExperiences = new java.util.ArrayList<>();
	    List<Project> superAdminProjects = new java.util.ArrayList<>();
	    List<Resume> superAdminResumes = new java.util.ArrayList<>();
	    List<Service> superAdminServices = new java.util.ArrayList<>();
	    List<Skill> superAdminSkills = new java.util.ArrayList<>();
	    
	    // 3. Loop through your super admins and harvest their mapped entities
	      List<String> roles= admin.getRoles();
	        if (admin.getEducationList() != null) superAdminEducation.addAll(admin.getEducationList());
	        if (admin.getExperiences() != null) superAdminExperiences.addAll(admin.getExperiences());
	        if (admin.getProjects() != null) superAdminProjects.addAll(admin.getProjects());
	        if (admin.getResumes() != null) superAdminResumes.addAll(admin.getResumes());
	        if (admin.getServices() != null) superAdminServices.addAll(admin.getServices());
	        if (admin.getSkills() != null) superAdminSkills.addAll(admin.getSkills());
	    
	    
	    // 4. Group everything nicely into the response map
	    Map<String, Object> superAdminData = new java.util.LinkedHashMap<>();
	    superAdminData.put("roles", roles);
	    superAdminData.put("education", superAdminEducation);
	    superAdminData.put("experiences", superAdminExperiences);
	    superAdminData.put("projects", superAdminProjects);
	    superAdminData.put("resumes", superAdminResumes);
	    superAdminData.put("services", superAdminServices);
	    superAdminData.put("skills", superAdminSkills);
	    superAdminData.put("loading", false);
	    
	    // 5. Structure the standard API Response
	    ResponseStructure<Map<String, Object>> responseStructure = new ResponseStructure<>();
	    responseStructure.setStatus(HttpStatus.OK.value());
	    responseStructure.setMessage("All Super Admin portfolio data retrieved successfully.");
	    responseStructure.setData(superAdminData);
	    
	    return new ResponseEntity<>(responseStructure, HttpStatus.OK);
	}
	
	
}
