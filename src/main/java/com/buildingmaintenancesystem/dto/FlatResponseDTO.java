package com.buildingmaintenancesystem.dto;

import com.buildingmaintenancesystem.entity.OccupancyStatus;
import lombok.Data;

@Data
public class FlatResponseDTO {
    private Long id;
    private String flatNumber;
    private String wing;
    private String floor;
    private String ownerName;
    private OccupancyStatus status;
}