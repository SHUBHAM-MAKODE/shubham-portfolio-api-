package com.portfolio.controller;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.ContactMessage;
import com.portfolio.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class ContactMessageController {

    @Autowired
    private ContactMessageService messageService;

    /**
     * PUBLIC ACCESS ENDPOINT: Public visitors sending contact submissions.
     * 🔓 PERMITTED: Explicitly allowed in SecurityConfig via permitAll().
     */
    @PostMapping("/send")
    public ResponseEntity<ResponseStructure<ContactMessage>> receiveMessage(@RequestBody ContactMessage message) {
        return messageService.saveMessage(message);
    }

    /**
     * SECURED INBOX: Fetch all contact message entries.
     * 👁️ BOTH ROLES: Accessible by SUPER_ADMIN and GUEST_VIEWER dashboards via GET matching.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<ContactMessage>>> fetchInbox() {
        return messageService.getAllMessages();
    }

    /**
     * TOGGLE READ STATUS: Update message tracking.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.PATCH interceptor.
     * Only headers with a valid SUPER_ADMIN signature can execute this.
     */
    @PatchMapping("/toggle-read/{id}")
    public ResponseEntity<ResponseStructure<ContactMessage>> markAsRead(@PathVariable Long id) {
        return messageService.toggleMessageReadStatus(id);
    }

    /**
     * PURGE MESSAGE: Delete message completely from database.
     * 🔒 RESTRICTED: Automatically blocked for GUEST_VIEWER by the HttpMethod.DELETE interceptor.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<String>> removeMessage(@PathVariable Long id) {
        return messageService.deleteMessage(id);
    }
}