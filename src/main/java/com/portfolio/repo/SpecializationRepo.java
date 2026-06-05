package com.portfolio.repo;

import com.portfolio.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepo extends JpaRepository<Specialization, Long> {
}