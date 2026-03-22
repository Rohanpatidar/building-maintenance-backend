package com.buildingmaintenancesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "flats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Flat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flatNumber;

    @Column(nullable = false)
    private String wing;

    private String floor;

    @Enumerated(EnumType.STRING)
    private OccupancyStatus status;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"flats", "password", "maintenanceBills"})
    private User owner;

    @OneToMany(mappedBy = "flat", cascade = CascadeType.ALL)
    // ✅ FIX 3: When listing bills, don't show the 'flat' object inside them again
    @JsonIgnoreProperties("flat")
    private List<MaintenanceBill> maintenanceBills;
}