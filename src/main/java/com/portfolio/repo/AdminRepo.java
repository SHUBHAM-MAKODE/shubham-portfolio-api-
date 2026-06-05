package com.portfolio.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.portfolio.entity.Admin;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Integer> {

	@Query("SELECT a FROM Admin a WHERE a.role =:role")
    List<Admin> findByRole(@Param("role") String role);

}
