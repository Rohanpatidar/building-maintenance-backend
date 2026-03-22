package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.entity.Notice;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.repository.NoticeRepository;
import com.buildingmaintenancesystem.repository.UserRepository;
import com.buildingmaintenancesystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Notice createNotice(@RequestBody Notice notice, Principal principal) {
        // 1. Link the Notice to the logged-in Admin
        User admin = userRepository.findByUsername(principal.getName()).orElse(null);
        notice.setAdmin(admin);

        Notice saved = noticeRepository.save(notice);
        List<User> residents = userRepository.findAll();
        for(User user : residents) {

            emailService.sendSimpleEmail(user.getEmail(),
                    "📢 New Society Notice: " + notice.getTitle(),
                    notice.getContent());
        }
        return saved;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteNotice(@PathVariable Long id) {
        noticeRepository.deleteById(id);
    }
}