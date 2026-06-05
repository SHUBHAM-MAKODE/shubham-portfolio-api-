package com.portfolio.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.entity.Service;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Integer>{

}
