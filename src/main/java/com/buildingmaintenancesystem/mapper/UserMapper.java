package com.buildingmaintenancesystem.mapper;

import com.buildingmaintenancesystem.dto.UserRegistrationRequest;
import com.buildingmaintenancesystem.dto.UserResponseDTO;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // 1. Request to Entity (For Registration)
    public User toEntity(UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // Service will BCrypt this
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(Role.valueOf(String.valueOf(request.getRole())));
        return user;
    }

    // 2. Entity to Response DTO (For Security)
    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        return dto;
    }
    public void updateEntityFromDTO(UserRegistrationRequest dto, User user) {
        if (dto == null || user == null) return;
        if (dto.getFullName()!= null) user.setFullName(dto.getFullName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());


    }
}