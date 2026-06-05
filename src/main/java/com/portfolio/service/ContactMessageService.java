package com.portfolio.service;

import com.portfolio.dto.ResponseStructure;
import com.portfolio.entity.ContactMessage;
import com.portfolio.repo.ContactMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepo messageRepo;

    // Public Endpoint Action: Visitors submitting messages from the frontend
    public ResponseEntity<ResponseStructure<ContactMessage>> saveMessage(ContactMessage message) {
        ResponseStructure<ContactMessage> structure = new ResponseStructure<>();
        message.setIsRead(false);        
        ContactMessage saved = messageRepo.save(message);
        
        structure.setStatus(HttpStatus.CREATED.value());
        structure.setMessage("Your inquiry has been logged successfully.");
        structure.setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }

    // Administrative Actions below (Secured)
    public ResponseEntity<ResponseStructure<List<ContactMessage>>> getAllMessages() {
        ResponseStructure<List<ContactMessage>> structure = new ResponseStructure<>();
        structure.setStatus(HttpStatus.OK.value());
        structure.setData(messageRepo.findAllByOrderByReceivedAtDesc());
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<ContactMessage>> toggleMessageReadStatus(Long id) {
        ResponseStructure<ContactMessage> structure = new ResponseStructure<>();
        Optional<ContactMessage> optional = messageRepo.findById(id);

        if (optional.isEmpty()) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            structure.setMessage("Message reference not found.");
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        ContactMessage msg = optional.get();
        msg.setIsRead(!msg.getIsRead()); // Flips true/false cleanly
        
        structure.setStatus(HttpStatus.OK.value());
        structure.setData(messageRepo.save(msg));
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<String>> deleteMessage(Long id) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        if (!messageRepo.existsById(id)) {
            structure.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
        }

        messageRepo.deleteById(id);
        structure.setStatus(HttpStatus.OK.value());
        structure.setMessage("Message purged successfully from inbox logs.");
        structure.setData("Deleted Key ID: " + id);
        return new ResponseEntity<>(structure, HttpStatus.OK);
    }
}