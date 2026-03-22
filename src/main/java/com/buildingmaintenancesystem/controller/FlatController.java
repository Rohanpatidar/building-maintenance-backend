package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.dto.FlatRequest;
import com.buildingmaintenancesystem.dto.FlatResponseDTO;
import com.buildingmaintenancesystem.dto.SocietyMemberDTO;
import com.buildingmaintenancesystem.entity.Flat;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.mapper.FlatMapper;
import com.buildingmaintenancesystem.repository.ExpenseRepository;
import com.buildingmaintenancesystem.repository.FlatRepository;
import com.buildingmaintenancesystem.service.ExcelExportService;
import com.buildingmaintenancesystem.service.FlatService;
import com.buildingmaintenancesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flats")
public class FlatController {
    @Autowired private FlatService flatService;
    @Autowired private FlatMapper flatMapper;
    @Autowired private UserService userService;
    @Autowired private FlatRepository flatRepository;
    @Autowired private ExcelExportService excelExportService;



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<FlatResponseDTO> addFlat(@RequestBody FlatRequest request) {
        User owner = null;

        // ✅ SAFETY CHECK: Only fetch User if ownerId is provided
        if (request.getOwnerId() != null) {
            owner = userService.getUserById(request.getOwnerId());
        }

        // Now map to Entity (Owner can be null, handled by Mapper)
        Flat flatEntity = flatMapper.toEntity(request, owner);

        // Call Service
        Flat savedFlat = flatService.addFlat(flatEntity);

        return ResponseEntity.ok(flatMapper.toResponseDTO(savedFlat));
    }
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")// This will now support the GET request you tried
    public ResponseEntity<List<FlatResponseDTO>> getAllFlats() {
        List<FlatResponseDTO> flats = flatService.getAllFlats().stream()
                .map(flatMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{flatnumber}")
    public ResponseEntity<FlatResponseDTO> getFlatByNumber(@PathVariable String flatnumber) {
        Flat flat = flatService.getFlatByNumber(flatnumber);
        FlatResponseDTO flatResponseDTO = flatMapper.toResponseDTO(flat);
        return ResponseEntity.ok(flatResponseDTO);

    }
    // Inside FlatController.java

    @PutMapping("/{flatId}/assign/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only Admin can do this
    public ResponseEntity<Flat> assignFlat(@PathVariable Long flatId, @PathVariable Long userId) {
        return ResponseEntity.ok(flatService.assignFlatToUser(flatId, userId));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Only Admin should delete flats!
    public ResponseEntity<String> deleteFlatById(@PathVariable Long id) {
        flatService.deleteFlat(id); // 👈 This actually performs the deletion
        return ResponseEntity.ok("Flat with ID " + id + " has been deleted successfully.");
    }
    @GetMapping("/my-flat")
    public ResponseEntity<?> getMyFlat(Principal principal) {
        // 1. Get the username from the Security Token
        String username = principal.getName();


        // 2. Find the user
        User user = userService.getUserByUsername(username);

        // 3. Find the flat assigned to this user
        // (Assuming you added findByOwnerId in FlatRepository)
        Optional<Flat> flat = flatRepository.findByOwnerId(user.getId());

        if (flat.isPresent()) {
            return ResponseEntity.ok(flat.get());
        } else {
            // Return 404 so React knows to show "No flat assigned"
            return ResponseEntity.status(404).body("No flat assigned yet.");
        }
    }
    @GetMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        List<Flat> flats = flatRepository.findAll();
        byte[] excelContent = excelExportService.exportResidentDirectory(flats);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Society_Directory.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }
    @GetMapping("/directory")
    public ResponseEntity<List<SocietyMemberDTO>> getSocietyDirectory() {
        List<SocietyMemberDTO> directory = flatRepository.findAll().stream()
                .map(flat -> new SocietyMemberDTO(
                        flat.getFlatNumber(),
                        flat.getWing(),
                        flat.getFloor(),
                        flat.getOwner() != null ? flat.getOwner().getFullName() : "N/A",
                        flat.getStatus().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(directory);
    }
}