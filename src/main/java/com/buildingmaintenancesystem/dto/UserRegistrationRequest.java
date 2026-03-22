package com.buildingmaintenancesystem.dto;

import com.buildingmaintenancesystem.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Full Name is mandatory")
    private String fullName;

    @NotNull(message = "Role is mandatory")
    private Role role;

    // ✅ NEW FIELD: Flat Number (Optional, because Admins might not have a flat)
    private String flatNumber;
}