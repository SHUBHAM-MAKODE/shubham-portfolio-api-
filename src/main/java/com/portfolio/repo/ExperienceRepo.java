package com.portfolio.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.entity.Experience;

@Repository
public interface ExperienceRepo extends JpaRepository<Experience, Integer> {

}
