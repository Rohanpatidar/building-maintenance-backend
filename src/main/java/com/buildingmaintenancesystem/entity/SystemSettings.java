package com.buildingmaintenancesystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
public class SystemSettings {
    @Id
    private Long id = 1L;

    private boolean autoBillingEnabled = false;
    private Double defaultAmount = 2500.0;
}