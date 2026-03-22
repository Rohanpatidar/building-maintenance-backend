package com.buildingmaintenancesystem.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillRequestDTO {
    private String flatNumber; // e.g. "401"
    private BigDecimal amount;
    private String description;
}