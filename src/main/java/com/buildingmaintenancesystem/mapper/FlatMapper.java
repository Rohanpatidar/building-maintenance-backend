package com.buildingmaintenancesystem.mapper;

import com.buildingmaintenancesystem.dto.FlatRequest;
import com.buildingmaintenancesystem.dto.FlatResponseDTO;
import com.buildingmaintenancesystem.entity.Flat;
import com.buildingmaintenancesystem.entity.OccupancyStatus;
import com.buildingmaintenancesystem.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FlatMapper {

    public Flat toEntity(FlatRequest dto, User owner) {
        Flat flat = new Flat();
        flat.setFlatNumber(dto.getFlatNumber());
        flat.setWing(dto.getWing());
        flat.setFloor(dto.getFloor());

        // ✅ Handle Owner
        flat.setOwner(owner); // If owner is null, it sets null (Perfect for Vacant)

        // ✅ Handle Status (String -> Enum)
        if (dto.getStatus() != null) {
            try {
                flat.setStatus(OccupancyStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (Exception e) {
                flat.setStatus(OccupancyStatus.VACANT); // Default fallback
            }
        } else {
            // Default logic
            flat.setStatus(owner != null ? OccupancyStatus.SELF_OCCUPIED : OccupancyStatus.VACANT);
        }

        return flat;
    }
    public FlatResponseDTO toResponseDTO(Flat flat) {
        if (flat == null) return null;
        FlatResponseDTO dto = new FlatResponseDTO();
        dto.setId(flat.getId());
        dto.setFlatNumber(flat.getFlatNumber());
        dto.setWing(flat.getWing());
        dto.setFloor(flat.getFloor());
        dto.setStatus(flat.getStatus());

        // Check if Owner exists before getting name to avoid NullPointerException
        if (flat.getOwner() != null) {
            // Ensure your User entity has a 'getFullName' or 'getUsername' method
            // If getFullName() doesn't exist, use getUsername()
            dto.setOwnerName(flat.getOwner().getUsername());
        } else {
            dto.setOwnerName("Unassigned");
        }
        return dto;
    }
}