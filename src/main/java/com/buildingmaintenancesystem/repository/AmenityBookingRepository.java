package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.entity.AmenityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityBookingRepository extends JpaRepository<AmenityBooking, Long> {
}