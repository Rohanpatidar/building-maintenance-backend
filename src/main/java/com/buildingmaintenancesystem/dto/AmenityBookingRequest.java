package com.buildingmaintenancesystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AmenityBookingRequest {
    private String amenityType; // CLUBHOUSE, GYM, etc.
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}