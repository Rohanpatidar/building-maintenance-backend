package com.buildingmaintenancesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private String status = "OPEN"; // OPEN, RESOLVED, CLOSED

    private String adminReply;      // Admin's message
    private Integer rating;         // 1 to 5 Stars

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    // ✅ Stop Infinite Loop but KEEP Username visible
    @JsonIgnoreProperties({"password", "flats", "roles"})
    private User user;
}