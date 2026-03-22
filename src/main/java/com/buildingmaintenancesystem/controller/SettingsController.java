package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.entity.SystemSettings;
import com.buildingmaintenancesystem.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance/settings")
@CrossOrigin("*")
public class SettingsController {

    @Autowired
    private SettingsRepository settingsRepository;

    // 1. Get current settings
    @GetMapping
    public ResponseEntity<SystemSettings> getSettings() {
        // Find row #1 or create it if the table is empty
        SystemSettings settings = settingsRepository.findById(1L)
                .orElseGet(() -> settingsRepository.save(new SystemSettings()));
        return ResponseEntity.ok(settings);
    }

    // 2. Toggle the switch
    @PutMapping("/toggle-auto-billing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemSettings> toggleAutoBilling(@RequestBody Map<String, Boolean> payload) {
        SystemSettings settings = settingsRepository.findById(1L)
                .orElseGet(() -> new SystemSettings());

        settings.setAutoBillingEnabled(payload.get("enabled"));
        return ResponseEntity.ok(settingsRepository.save(settings));
    }
}