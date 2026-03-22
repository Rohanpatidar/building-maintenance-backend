package com.buildingmaintenancesystem.mapper;

import com.buildingmaintenancesystem.dto.AmenityBookingRequest;
import com.buildingmaintenancesystem.dto.AmenityResponseDTO;
import com.buildingmaintenancesystem.entity.AmenityBooking;
import com.buildingmaintenancesystem.entity.AmenityType;
import org.springframework.stereotype.Component;

@Component
public class AmenityBookingMapper {
    public AmenityBooking toEntity(AmenityBookingRequest request) {
        AmenityBooking booking = new AmenityBooking();
        booking.setAmenityType(AmenityType.valueOf(request.getAmenityType()));
        booking.setBookingStartTime(request.getStartTime());
        booking.setBookingEndTime(request.getEndTime());
        return booking;
    }
    public AmenityResponseDTO toResponseDTO(AmenityBooking booking) {
        if (booking == null) return null;
        AmenityResponseDTO dto = new AmenityResponseDTO();
        dto.setId(booking.getId());
        dto.setAmenityType(booking.getAmenityType().name());
        dto.setStartTime(booking.getBookingStartTime());
        dto.setEndTime(booking.getBookingEndTime());
        dto.setStatus(booking.getStatus().name());
        return dto;
    }
}