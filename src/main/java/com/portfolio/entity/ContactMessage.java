package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // 🌟 FIXED: Changed 'boolean' to 'Boolean'

    @PrePersist
    protected void onCreate() {
        this.receivedAt = LocalDateTime.now();
        
        // Safety checkpoint handler if deserialization leaves the wrapper null
        if (this.isRead == null) {
            this.isRead = false;
        }
    }
}