package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.entity.AmenityBooking;
import com.buildingmaintenancesystem.repository.AmenityBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AmenityBookingService {

    @Autowired
    private AmenityBookingRepository bookingRepository;

    public AmenityBooking bookAmenity(AmenityBooking booking) {
        // Basic Business Rule: Ensure end time is after start time
        if (booking.getBookingEndTime().isBefore(booking.getBookingStartTime())) {
            throw new RuntimeException("End time cannot be before start time");
        }

        // Advanced Logic: Here you would check MySQL to see if the Gym/Hall
        // is already booked for these specific hours.

        return bookingRepository.save(booking);
    }

    public List<AmenityBooking> getAllBookings() {
        return bookingRepository.findAll();
    }
}