package com.buildingmaintenancesystem.dto;

import lombok.Data;

@Data
public class FlatRequest {
    private String flatNumber;
    private String wing;
    private String floor;

    // Optional: Can be null if flat is VACANT
    private Long ownerId;

    // ✅ Changed to String to prevent deserialization errors
    // Value can be: "VACANT", "OCCUPIED", "SELF_OCCUPIED", "TENANT_OCCUPIED"
    private String status;
}