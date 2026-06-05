package com.portfolio.repo;

import com.portfolio.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactMessageRepo extends JpaRepository<ContactMessage, Long> {
    
    // Sorts messages so the newest inquiries appear at the top of your inbox
    List<ContactMessage> findAllByOrderByReceivedAtDesc();
    
    // Quick count utility to show an unread notification badge on your dashboard sidebar
    long countByIsReadFalse();
}