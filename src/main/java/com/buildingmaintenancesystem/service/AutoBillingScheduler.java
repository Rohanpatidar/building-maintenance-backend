package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.dto.BillGenerationRequest;
import com.buildingmaintenancesystem.entity.SystemSettings;
import com.buildingmaintenancesystem.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AutoBillingScheduler {

    private final SettingsRepository settingsRepository;
    private final FinancialService financialService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void runMonthlyBilling() {
        SystemSettings settings = settingsRepository.findById(1L).orElse(new SystemSettings());

        if (settings.isAutoBillingEnabled()) {
            String currentMonth = LocalDate.now().getMonth().toString() + "-" + LocalDate.now().getYear();
            Double amount = settings.getDefaultAmount();
            BillGenerationRequest request = new BillGenerationRequest(currentMonth, amount);

            // ✅ Trigger for all three categories
            financialService.generateBillsByStatus(request, "SELF_OCCUPIED");
            financialService.generateBillsByStatus(request, "RENTED");
            financialService.generateBillsByStatus(request, "VACANT");

            System.out.println("🚀 Full Auto-Billing completed for all flats: " + currentMonth);
        }
    }
}