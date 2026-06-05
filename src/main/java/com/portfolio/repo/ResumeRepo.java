package com.portfolio.repo;

import com.portfolio.entity.Resume;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface ResumeRepo extends JpaRepository<Resume, Long> {

    // Native utility query to reset active flags when a new primary resume is selected
    @Transactional
    @Modifying
    @Query("UPDATE Resume r SET r.isActive = false")
    void clearActiveFlags();
    
    Optional<Resume> findByIsActiveTrue();
}