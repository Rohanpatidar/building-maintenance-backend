package com.buildingmaintenancesystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ComplaintRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 5, max = 100, message = "Title must be between 5-100 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;
}