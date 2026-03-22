package com.buildingmaintenancesystem.entity;

import com.buildingmaintenancesystem.enums.BillStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_bills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MaintenanceBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String month;
    private LocalDate generatedDate;
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private BillStatus status = BillStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    // ✅ FIX 1: Don't show password or full flat list of user
    @JsonIgnoreProperties({"password", "flats", "roles", "maintenanceBills"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "flat_id")
    // ✅ FIX 2: Don't show the 'maintenanceBills' list inside the Flat object
    // This stops the Infinite Loop!
    @JsonIgnoreProperties({"maintenanceBills", "owner", "payments"})
    private Flat flat;
}