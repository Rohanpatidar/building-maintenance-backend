package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // We want the newest notices first!
    List<Notice> findAllByOrderByCreatedAtDesc();
}