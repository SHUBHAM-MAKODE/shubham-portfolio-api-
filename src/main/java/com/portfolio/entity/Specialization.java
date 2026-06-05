package com.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "specializations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // e.g., "Full Stack Java Architecture"

    @Column(nullable = false)
    private String proficiency; // e.g., "90"

    // Maps your assigned project details array as a child collection table
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "specialization_details", joinColumns = @JoinColumn(name = "specialization_id"))
    @Column(name = "detail_text", columnDefinition = "TEXT")
    private List<String> details; 

    @Column(nullable = false, length = 500)
    private String logoUrl; 
    
    @Column(nullable = false)
    private String logoPublicId; 

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}