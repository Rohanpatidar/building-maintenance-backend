package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.entity.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpRecord, Long> {

    // Find the latest OTP sent to this email
    Optional<OtpRecord> findTopByEmailOrderByExpiryTimeDesc(String email);

    // Delete all old OTPs for an email after success
    void deleteByEmail(String email);
}