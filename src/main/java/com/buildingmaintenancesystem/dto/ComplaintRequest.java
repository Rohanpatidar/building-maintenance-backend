package com.buildingmaintenancesystem.dto;

import lombok.Data;

@Data
public class ComplaintRequest {
    // These names MUST match the frontend exactly
    private String title;
    private String description;
}