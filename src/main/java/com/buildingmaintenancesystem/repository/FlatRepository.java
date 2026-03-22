package com.buildingmaintenancesystem.repository;

import com.buildingmaintenancesystem.entity.Flat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {
    List<Flat> findByWing(String wing);
    Flat findByFlatNumber(String flatNumber);
    @Query("SELECT f FROM Flat f WHERE f.owner.username= :username")
    Optional<Flat> findByUserUsername(@Param("username") String username);
    // Inside FlatRepository interface
    Optional<Flat> findByOwnerId(Long ownerId);
}