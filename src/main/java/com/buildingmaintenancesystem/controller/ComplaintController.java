package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.dto.ComplaintRequest;
import com.buildingmaintenancesystem.entity.Complaint;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.repository.ComplaintRepository;
import com.buildingmaintenancesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private UserRepository userRepository;


    @GetMapping
    public List<Complaint> getComplaints() {
        // Simple and Transparent: Return ALL complaints for everyone
        // We no longer need 'Principal' because we don't check who is asking
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    // 2. User Raises a Complaint
    @PostMapping
    public Complaint raiseComplaint(@RequestBody ComplaintRequest request, Principal principal) {
        // Find the user who is logged in
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create the Entity manually from the DTO
        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setUser(user);
        complaint.setStatus("OPEN");
        complaint.setCreatedAt(LocalDateTime.now());

        return complaintRepository.save(complaint);
    }

    // 3. Admin Resolves the Complaint
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public Complaint resolveComplaint(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Complaint c = complaintRepository.findById(id).orElseThrow();
        c.setAdminReply(body.get("reply")); // Get reply from JSON
        c.setStatus("RESOLVED");
        return complaintRepository.save(c);
    }

    // 4. User Rates and Closes the Complaint
    @PutMapping("/{id}/rate")
    public Complaint rateComplaint(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Complaint c = complaintRepository.findById(id).orElseThrow();
        c.setRating(body.get("rating")); // Get rating (1-5)
        c.setStatus("CLOSED");
        return complaintRepository.save(c);
    }
    // 1. Get Complaints (PUBLIC: Everyone sees everything now)

}