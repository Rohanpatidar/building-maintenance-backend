package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.dto.BillGenerationRequest;
import com.buildingmaintenancesystem.dto.FinancialReportDTO;
import com.buildingmaintenancesystem.entity.*;
import com.buildingmaintenancesystem.enums.BillStatus;
import com.buildingmaintenancesystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinancialService {

    @Autowired private MaintenanceRepository maintenanceRepository; // ✅ Correct Repo
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private FlatRepository flatRepository;
    @Autowired private PdfService pdfService;
    @Autowired private EmailService emailService;


    // 1. Generate Bills
    // Inside FinancialService.java

    // Inside FinancialService.java


    public String generateBillsByStatus(BillGenerationRequest request, String statusFilter) {
        OccupancyStatus targetStatus = OccupancyStatus.valueOf(statusFilter.toUpperCase());
        List<Flat> targetFlats = flatRepository.findAll().stream()
                .filter(f -> f.getStatus() == targetStatus && f.getOwner() != null)
                .toList();

        if (targetFlats.isEmpty()) return "No eligible flats found.";

        int count = 0;
        int skipped = 0;

        for (Flat flat : targetFlats) {
            boolean exists = maintenanceRepository.findAll().stream()
                    .anyMatch(b -> b.getFlat().getId().equals(flat.getId()) &&
                            b.getMonth().equalsIgnoreCase(request.getMonth()));
            if (exists) {
                skipped++;
                continue;
            }

            MaintenanceBill bill = new MaintenanceBill();
            bill.setAmount(request.getAmount());
            bill.setMonth(request.getMonth().toUpperCase());
            bill.setGeneratedDate(LocalDate.now());
            bill.setStatus(BillStatus.PENDING);
            bill.setFlat(flat);
            bill.setUser(flat.getOwner());

            maintenanceRepository.save(bill);
            count++;

            // 👈 2. Send Bill Generation Alert Email
            if (flat.getOwner().getEmail() != null) {
                String subject = "🔔 Maintenance Bill Generated - " + request.getMonth();
                String body = "Dear " + flat.getOwner().getFullName() + ",\n\n" +
                        "Your maintenance bill for " + request.getMonth() + " of amount ₹" + request.getAmount() +
                        " has been generated. Please login to the portal to make the payment.\n\n" +
                        "Regards,\nSociety Management";
                emailService.sendSimpleEmail(flat.getOwner().getEmail(), subject, body);
            }
        }
        return "Generated: " + count + " bills. Emails sent.";
    }

    public MaintenanceBill payBill(Long billId) {
        MaintenanceBill bill = maintenanceRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setStatus(BillStatus.PAID);
        bill.setPaymentDate(LocalDate.now());
        MaintenanceBill savedBill = maintenanceRepository.save(bill);

        // 👈 3. Send Payment Receipt Email with PDF Attachment
        if (bill.getUser().getEmail() != null) {
            try {
                byte[] pdfContent = pdfService.generateMaintenanceReceipt(savedBill);
                String subject = "✅ Payment Successful - Receipt: " + bill.getMonth();
                String body = "Dear " + bill.getUser().getFullName() + ",\n\n" +
                        "Thank you for your payment of ₹" + bill.getAmount() + " for the month of " + bill.getMonth() + ".\n" +
                        "Please find your digital receipt attached to this email.\n\n" +
                        "Regards,\nBuilding Maintenance System";

                emailService.sendEmailWithAttachment(
                        bill.getUser().getEmail(),
                        subject,
                        body,
                        pdfContent,
                        "Receipt_" + bill.getMonth() + ".pdf"
                );
            } catch (Exception e) {
                System.err.println("Failed to send receipt email: " + e.getMessage());
            }
        }
        return savedBill;
    }
// Add this method to FinancialService.java

    public Map<String, Object> getBalanceSheet() {
        // 1. Calculate Total Income (Sum of all PAID maintenance bills)
        Double totalIncome = maintenanceRepository.findAll().stream()
                .filter(bill -> bill.getStatus() == BillStatus.PAID)
                .mapToDouble(MaintenanceBill::getAmount)
                .sum();

        // 2. Calculate Total Expenses
        Double totalExpenses = expenseRepository.findAll().stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // 3. Prepare the Map
        Map<String, Object> report = new HashMap<>();
        report.put("totalIncome", totalIncome);
        report.put("totalExpenses", totalExpenses);
        report.put("balance", totalIncome - totalExpenses);

        return report;
    }


    // 3. Add Expense
    public Expense addExpense(Expense expense) {
        expense.setExpenseDate(LocalDate.now());
        return expenseRepository.save(expense);
    }

    // 4. Get User Bills
    public List<MaintenanceBill> getUserBills(Long userId) {
        return maintenanceRepository.findByUserId(userId);
    }

    // 5. Get Public Report
    public FinancialReportDTO getFinancialReport() {
        Double totalIncome = maintenanceRepository.calculateTotalIncome();
        if (totalIncome == null) totalIncome = 0.0; // Handle null safety

        Double totalExpense = expenseRepository.findAll().stream()
                .mapToDouble(Expense::getAmount).sum();

        FinancialReportDTO report = new FinancialReportDTO();
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setCurrentBalance(totalIncome - totalExpense);
        return report;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
}