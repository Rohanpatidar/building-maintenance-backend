package com.buildingmaintenancesystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AmenityResponseDTO {
    private Long id;
    private String amenityType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}