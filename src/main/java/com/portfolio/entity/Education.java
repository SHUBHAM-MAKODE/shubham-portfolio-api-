package com.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "education_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String duration; // e.g., "2024 - 2027 (Expected)"

    @Column(nullable = false)
    private String grade;    // e.g., "Pursuing" or "Graduated"

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "education_details", joinColumns = @JoinColumn(name = "education_id"))
    @Column(name = "detail_text", columnDefinition = "TEXT")
    private List<String> details; // Dynamic array handling accordion description lists

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}