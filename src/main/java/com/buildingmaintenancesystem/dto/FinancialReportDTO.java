package com.buildingmaintenancesystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class FinancialReportDTO {
    private Double totalIncome;    // Sum of PAID bills
    private Double totalExpense;   // Sum of Expenses
    private Double currentBalance; // Income - Expense
    // We can list recent transactions here if needed
}