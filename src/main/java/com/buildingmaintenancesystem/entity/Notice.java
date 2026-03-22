package com.buildingmaintenancesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // 👈 1. IMPORT THIS
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createdAt;

    // 🛑 THIS CAUSED THE LOOP
    // ✅ FIX: Add @JsonIgnore here
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonIgnore  // 👈 2. ADD THIS LINE
    private User admin;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}