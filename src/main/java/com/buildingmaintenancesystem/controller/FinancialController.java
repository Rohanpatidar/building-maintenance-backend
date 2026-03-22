package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.dto.BillGenerationRequest;
import com.buildingmaintenancesystem.dto.FinancialReportDTO;
import com.buildingmaintenancesystem.entity.Expense;
import com.buildingmaintenancesystem.entity.MaintenanceBill;
import com.buildingmaintenancesystem.repository.ExpenseRepository;
import com.buildingmaintenancesystem.repository.MaintenanceRepository;
import com.buildingmaintenancesystem.service.FinancialService;
import com.buildingmaintenancesystem.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinancialController {

    @Autowired private FinancialService financialService;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private MaintenanceRepository maintenanceRepository; // Ensure this is autowired
    @Autowired private PdfService pdfService;

    // Generate Bills
    // Inside FinancialController.java

    @PostMapping("/bills/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateBills(
            @RequestBody BillGenerationRequest request,
            @RequestParam String status) { // 👈 Add this parameter
        return ResponseEntity.ok(financialService.generateBillsByStatus(request, status));
    }
// Inside FinancialController.java

    @DeleteMapping("/bills/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBill(@PathVariable Long id) {
        try {
            // Use the custom method we just created
            maintenanceRepository.deleteByBillId(id);
            return ResponseEntity.ok("Bill deleted successfully");
        } catch (Exception e) {
            // Log the error to your IntelliJ console to see the REAL cause
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    // Inside FinancialController.java

    @PostMapping("/expenses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        expense.setExpenseDate(LocalDate.now()); // Set today's date automatically
        return ResponseEntity.ok(expenseRepository.save(expense));
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    @DeleteMapping("/expenses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return ResponseEntity.ok("Expense deleted");
    }


    // Get My Bills
    @GetMapping("/bills/user/{userId}")
    public ResponseEntity<List<MaintenanceBill>> getUserBills(@PathVariable Long userId) {
        return ResponseEntity.ok(financialService.getUserBills(userId));
    }
    @GetMapping("/balance-sheet")
    public ResponseEntity<Map<String, Object>> getBalanceSheet() {
        return ResponseEntity.ok(financialService.getBalanceSheet());
    }
    @GetMapping("/bills/{id}/download")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long id) {
        MaintenanceBill bill = maintenanceRepository.findById(id).orElseThrow();
        byte[] pdfContent = pdfService.generateMaintenanceReceipt(bill);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Receipt_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    // Pay Bill
    @PutMapping("/bills/{billId}/pay")
    public ResponseEntity<MaintenanceBill> payBill(@PathVariable Long billId) {
        return ResponseEntity.ok(financialService.payBill(billId));
    }

    // Get Report
    @GetMapping("/report")
    public ResponseEntity<FinancialReportDTO> getReport() {
        return ResponseEntity.ok(financialService.getFinancialReport());
    }



//    Now both ADMIN and USER can see this for Transparency
    @GetMapping("/bills")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<MaintenanceBill>> getAllBills() {
        return ResponseEntity.ok(maintenanceRepository.findAll());
    }
}