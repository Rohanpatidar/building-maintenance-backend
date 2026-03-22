package com.buildingmaintenancesystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComplaintResponseDTO {
    private Long id;
    private String subject;
    private String description;
    private String status; // OPEN, RESOLVED, etc.
    private LocalDateTime createdAt;
}