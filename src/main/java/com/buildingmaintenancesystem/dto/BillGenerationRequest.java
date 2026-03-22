package com.buildingmaintenancesystem.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillGenerationRequest {
    private String month;
    private Double amount;
    // "FEB-2026"


}