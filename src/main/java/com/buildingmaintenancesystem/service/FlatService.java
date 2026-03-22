package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.dto.FlatRequest;
import com.buildingmaintenancesystem.dto.FlatResponseDTO;
import com.buildingmaintenancesystem.entity.Flat;
import com.buildingmaintenancesystem.entity.OccupancyStatus;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.mapper.FlatMapper;
import com.buildingmaintenancesystem.repository.FlatRepository;
import com.buildingmaintenancesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class FlatService {
    @Autowired private FlatRepository flatRepository;
    @Autowired private FlatMapper flatMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    public Flat addFlat(Flat flat) {
        // 1. Check if Flat Number exists
        if (flatRepository.findByFlatNumber(flat.getFlatNumber()) != null) {
            throw new RuntimeException("Flat Number " + flat.getFlatNumber() + " already exists!");
        }

        // 2. Set Status Logic (If logic wasn't in Mapper)
        if (flat.getStatus() == null) {
            // Auto-detect status if missing
            if (flat.getOwner() != null) {
                flat.setStatus(OccupancyStatus.SELF_OCCUPIED);
            } else {
                flat.setStatus(OccupancyStatus.VACANT);
            }
        }

        return flatRepository.save(flat);
    }

    // Additional helpful method
    public Flat getFlatById(Long id) {
        return flatRepository.findById(id).orElseThrow(() -> new RuntimeException("Flat not found"));
    }
    public List<Flat> getAllFlats() {
        return flatRepository.findAll();
    }
    public Flat getFlatByNumber(String flatnumber) {
        Flat flat = flatRepository.findByFlatNumber(flatnumber);
        if (flat != null) {
            return flat;
        }else  {
            throw new RuntimeException("Flat not found");
        }

    }
    public void deleteFlat(Long id) {
        // Check if flat exists before deleting
        if (!flatRepository.existsById(id)) {
            throw new RuntimeException("Flat not found with id: " + id);
        }
        flatRepository.deleteById(id);
    }
    public FlatResponseDTO getFlatByUsername(String username) {
        Flat flat = flatRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("No flat found for user: " + username));

        // Convert Entity -> DTO
        return flatMapper.toResponseDTO(flat);
    }
    public Flat assignFlatToUser(Long flatId, Long userId) {
        // 1. Fetch Flat
        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(() -> new RuntimeException("Flat not found"));

        // 2. Safety Check: Is it already occupied?
        if (flat.getOwner() != null) {
            throw new RuntimeException("Flat " + flat.getFlatNumber() + " is already occupied by " + flat.getOwner().getUsername());
        }

        // 3. Fetch User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Link them
        flat.setOwner(user);
        flat.setStatus(OccupancyStatus.SELF_OCCUPIED); // Update Status

        // 5. Save
        return flatRepository.save(flat);
    }
    @GetMapping("/my-flat")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getMyFlat(Principal principal) {
        // 1. Get currently logged-in user
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        // 2. Find their flat
        Optional<Flat> flat = flatRepository.findByOwnerId(user.getId());

        if (flat.isPresent()) {
            return ResponseEntity.ok(flat.get());
        } else {
            return ResponseEntity.status(404).body("No flat assigned to you yet.");
        }
    }
    }