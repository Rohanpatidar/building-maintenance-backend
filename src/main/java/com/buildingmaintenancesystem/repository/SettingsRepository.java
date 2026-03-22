package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<SystemSettings, Long> {
}