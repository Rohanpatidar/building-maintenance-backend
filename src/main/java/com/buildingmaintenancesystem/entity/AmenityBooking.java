package com.buildingmaintenancesystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "amenity_bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AmenityBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmenityType amenityType;

    @Column(nullable = false)
    private LocalDateTime bookingStartTime;

    @Column(nullable = false)
    private LocalDateTime bookingEndTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User bookedBy;

}

