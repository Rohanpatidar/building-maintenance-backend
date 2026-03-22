package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.dto.AmenityBookingRequest;
import com.buildingmaintenancesystem.dto.AmenityResponseDTO;
import com.buildingmaintenancesystem.entity.AmenityBooking;
import com.buildingmaintenancesystem.mapper.AmenityBookingMapper;
import com.buildingmaintenancesystem.service.AmenityBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class AmenityBookingController {
    @Autowired private AmenityBookingService amenityService;
    @Autowired private AmenityBookingMapper amenityMapper;

    @PostMapping
    public ResponseEntity<AmenityResponseDTO> book(@RequestBody AmenityBookingRequest request) {
        AmenityBooking booking = amenityService.bookAmenity(amenityMapper.toEntity(request));
        return ResponseEntity.ok(amenityMapper.toResponseDTO(booking));
    }
}