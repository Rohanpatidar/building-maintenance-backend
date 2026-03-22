package com.buildingmaintenancesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocietyMemberDTO {
    private String flatNumber;
    private String wing;
    private String floor;
    private String ownerName;
    private String status; // OCCUPIED, RENTED, VACANT
}