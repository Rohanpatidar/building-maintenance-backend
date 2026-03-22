package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // For Users: See only their own complaints
    List<Complaint> findByUserUsernameOrderByCreatedAtDesc(String username);

    // For Admin: See ALL complaints (Newest first)
    List<Complaint> findAllByOrderByCreatedAtDesc();
}