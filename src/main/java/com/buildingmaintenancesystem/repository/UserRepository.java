package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.dto.UserRegistrationRequest;
import com.buildingmaintenancesystem.entity.Flat;
import com.buildingmaintenancesystem.entity.Role;
import com.buildingmaintenancesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom method to find a user
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

    // Also helpful for your OTP logic to check if email exists
    boolean existsByEmail(String email);

    boolean existsByRole(Role role);




}