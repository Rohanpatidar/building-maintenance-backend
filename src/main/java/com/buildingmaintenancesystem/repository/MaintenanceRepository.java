package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.entity.MaintenanceBill;
import com.buildingmaintenancesystem.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceBill, Long> {

    // 1. Find all bills for a specific user (User Dashboard)
    // Ordered by latest generated date
    @Query("SELECT m FROM MaintenanceBill m WHERE m.user.username = :username ORDER BY m.generatedDate DESC")
    List<MaintenanceBill> findMyBills(@Param("username") String username);

    // 2. Find by User ID
    List<MaintenanceBill> findByUserId(Long userId);

    // 3. Find by Status (e.g., PENDING or PAID)
    List<MaintenanceBill> findByStatus(BillStatus status);

    // 4. Count bills by status (Optimized for Dashboard)
    long countByStatus(BillStatus status);

    // 5. Sum of all PAID bills (For Dashboard Total Balance)
    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM MaintenanceBill m WHERE m.status = 'PAID'")
    Double calculateTotalIncome();
    @Modifying
    @Transactional
    @Query("DELETE FROM MaintenanceBill m WHERE m.id = :id")
    void deleteByBillId(Long id);
}