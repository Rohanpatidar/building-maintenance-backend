package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.enums.BillStatus;
import com.buildingmaintenancesystem.repository.FlatRepository;
import com.buildingmaintenancesystem.repository.MaintenanceRepository; // ✅ Changed from PaymentRepository
import com.buildingmaintenancesystem.repository.UserRepository;
import com.buildingmaintenancesystem.repository.ExpenseRepository; // ✅ Added to calculate Net Balance
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired private UserRepository userRepository;
    @Autowired private FlatRepository flatRepository;
    @Autowired private MaintenanceRepository maintenanceRepository; // ✅ Updated
    @Autowired private ExpenseRepository expenseRepository; // ✅ Optional: for Net Balance

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Total Residents (Count of Users)
        stats.put("totalResidents", userRepository.count());

        // 2. Total Flats
        stats.put("totalFlats", flatRepository.count());

        // 3. Pending Bills Count (Using the optimized Count method)
        long pendingCount = maintenanceRepository.countByStatus(BillStatus.PENDING);
        stats.put("pendingBills", pendingCount);

        // 4. Total Income (Sum of PAID bills)
        Double totalIncome = maintenanceRepository.calculateTotalIncome();

        // 5. Total Expenses (Sum of all Expenses)
        Double totalExpense = expenseRepository.findAll().stream()
                .mapToDouble(e -> e.getAmount())
                .sum();

        // 6. Net Society Balance (Income - Expense)
        stats.put("societyBalance", totalIncome - totalExpense);

        // (Optional) Just total income if you don't want to subtract expenses yet
        stats.put("totalCollected", totalIncome);

        return ResponseEntity.ok(stats);
    }
}