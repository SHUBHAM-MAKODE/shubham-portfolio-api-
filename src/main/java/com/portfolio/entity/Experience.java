package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String duration;

    private String location;

    @ElementCollection
    @CollectionTable(name = "experience_description_points", joinColumns = @JoinColumn(name = "experience_id"))
    @Column(name = "point_text", columnDefinition = "TEXT")
    private List<String> descriptionPoints;

    @Column(length = 500)
    private String companyLogoUrl; 
    
    private String companyLogoPublicId; 

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}