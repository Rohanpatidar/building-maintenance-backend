package com.buildingmaintenancesystem.controller;

import com.buildingmaintenancesystem.dto.*;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.mapper.UserMapper;
import com.buildingmaintenancesystem.security.JwtUtils;
import com.buildingmaintenancesystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils jwtUtils;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        try {
            User newUser = userService.registerUser(request);
            return ResponseEntity.ok("User registered successfully! ID: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Authenticate the User
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Fetch the User from DB to get their ID
        User user = userService.getUserByUsername(request.getUsername());

        // 3. Extract the Role
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // 4. Generate Token (Pass Username, Role, AND User ID)
        // Note: Make sure you updated JwtUtils as we discussed in the last step!
        String jwt = jwtUtils.generateToken(user.getUsername(), role, user.getId());

        // 5. Return a proper JSON object with everything the frontend needs
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("id", user.getId()); // 👈 This fixes the null userId in React
        response.put("role", role);
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Optional: Restrict getting all users to Admin
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> responseList = users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUsersByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        UserResponseDTO responseDTO = userMapper.toResponseDTO(user);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Usually only Admin deletes users
    public ResponseEntity<HttpStatus> deleteUserByUsername(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserResponseDTO> updateUserDetails(
            @PathVariable Long id,
            @RequestBody UserRegistrationRequest dto ,Authentication authentication) {
        // This calls your existing method that uses the Mapper
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }
    @PostMapping("/forgot-password/request")
    public ResponseEntity<String> requestOtp(@RequestParam String email) { // 👈 Must be @RequestParam
        return ResponseEntity.ok(userService.generateAndSendOtp(email));
    }
    // 1. Forgot Username
    @PostMapping("/forgot-username")
    public ResponseEntity<String> forgotUsername(@RequestParam String email) {
        return ResponseEntity.ok(userService.sendForgottenUsername(email));
    }
    @PutMapping("/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')") // 👈 Crucial: ONLY an existing Admin can do this!
    public ResponseEntity<String> promoteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToAdmin(id));
    }
    // 2. Reset Password (The final step)
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.verifyAndResetPassword(email, otp, newPassword));
    }
}