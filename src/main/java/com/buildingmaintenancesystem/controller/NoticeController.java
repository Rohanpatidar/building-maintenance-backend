package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.entity.Notice;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.repository.NoticeRepository;
import com.buildingmaintenancesystem.repository.UserRepository;
import com.buildingmaintenancesystem.service.EmailService;
import com.buildingmaintenancesystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notices") // 👈 This + axiosConfig = /api/notices
public class NoticeController {

    @Autowired private NoticeRepository noticeRepository;
    @Autowired private  UserRepository userRepository;
    @Autowired private EmailService emailService;// To find the Admin
    @Autowired private UserService userService;
    @GetMapping
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notice> createNotice(@Valid @RequestBody Notice notice, Principal principal) {
//        // 1. Link the Notice to the logged-in Admin
//        User admin = userRepository.findByUsername(principal.getName()).orElse(null);
//        notice.setAdmin(admin);
//
//        Notice saved = noticeRepository.save(notice);
//        List<User> residents = userRepository.findAll();
//        for(User user : residents) {
//
//            emailService.sendSimpleEmail(user.getEmail(),
//                    "📢 New Society Notice: " + notice.getTitle(),
//                    notice.getContent());
//        }
//        return saved;

        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User admin = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        notice.setAdmin(admin);
        Notice saved = noticeRepository.save(notice);

        // Yahan loop ki jagah service call honi chahiye jo @Async ho
        userService.notifyAllResidents(notice.getTitle(), notice.getContent());

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteNotice(@PathVariable Long id) {
        noticeRepository.deleteById(id);
    }
}