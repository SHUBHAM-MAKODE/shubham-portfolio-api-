package com.portfolio.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // e.g., "Main Technical Resume", "SDE Core Resume"

    @Column(nullable = false, length = 500)
    private String downloadUrl; // Secure destination download link on Cloudinary

    @Column(nullable = false)
    private String publicId; // Track identity string to execute remote file drops

    @Column(nullable = false)
    private boolean isActive = false; // Flag to designate which file renders on the main page
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    // This JPA lifecycle hook guarantees the column is never null by auto-stamping before saving!
    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}