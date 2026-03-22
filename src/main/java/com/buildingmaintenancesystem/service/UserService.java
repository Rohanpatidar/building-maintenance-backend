package com.buildingmaintenancesystem.service;

import com.buildingmaintenancesystem.dto.UserRegistrationRequest;
import com.buildingmaintenancesystem.dto.UserResponseDTO;
import com.buildingmaintenancesystem.entity.*;
import com.buildingmaintenancesystem.mapper.UserMapper;
import com.buildingmaintenancesystem.repository.FlatRepository;
import com.buildingmaintenancesystem.repository.OtpRepository;
import com.buildingmaintenancesystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    UserMapper userMapper = new UserMapper();

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlatRepository flatRepository;
    @Autowired private OtpRepository otpRepository;
    @Autowired private EmailService emailService;

    @Transactional
    public String promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
        return user.getFullName() + " is now an Admin!";
    }
    public User registerUser(UserRegistrationRequest request) {
        // 1. Check if Username Exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken!");
        }

        // 2. Create User Object
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt Password
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(Role.ROLE_USER); // Set Enum Role

        // 3. Save User to Generate ID
        User savedUser = userRepository.save(user);

        // 4. Link Flat (Only if Role is USER and Flat Number is provided)
        if (request.getRole() == Role.ROLE_USER &&
                request.getFlatNumber() != null && !request.getFlatNumber().isEmpty()) {

            Flat flat = flatRepository.findByFlatNumber(request.getFlatNumber());

            if (flat != null) {
                if (flat.getOwner() != null) {
                    throw new RuntimeException("Flat " + request.getFlatNumber() + " is already occupied!");
                }
                flat.setOwner(savedUser); // Assign Owner
                flat.setStatus(OccupancyStatus.SELF_OCCUPIED); // Update Status
                flatRepository.save(flat);
            } else {
                throw new RuntimeException("Flat Number " + request.getFlatNumber() + " not found!");
            }
        }

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username not found!"));
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with Id: " + id));
    }
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }




    // --- YOUR EXISTING UPDATE METHOD ---
    public UserResponseDTO updateUser(Long id, UserRegistrationRequest dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userMapper.updateEntityFromDTO(dto, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    // --- FORGET USERNAME ---
    public String sendForgottenUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user registered with this email"));

        emailService.sendSimpleEmail(email, "👤 Your Society Portal Username",
                "Hello " + user.getFullName() + ",\n\nYour username for the Building Maintenance System is: " + user.getUsername());
        return "Username sent to email.";
    }

    // --- FORGET PASSWORD: STEP 1 (Generate & Send OTP) ---
    @Transactional
    public String generateAndSendOtp(String email) {
        // 1. Check if user exists (use Optional correctly)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // 2. Generate 6-digit OTP
        String otp = String.format("%06d", new java.util.Random().nextInt(1000000));

        // 3. Save OTP with 5-minute expiry
        OtpRecord record = new OtpRecord();
        record.setEmail(email);
        record.setOtp(otp);
        record.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        try {
            otpRepository.save(record);
        } catch (Exception e) {
            throw new RuntimeException("Database error: Could not save OTP. Check if otp_records table exists.");
        }

        // 4. Send Email
        try {
            emailService.sendSimpleEmail(email, "🔑 Password Reset OTP",
                    "Your OTP is: " + otp + "\nThis code is valid for 5 minutes.");
        } catch (Exception e) {
            throw new RuntimeException("Email service failed. Check your SMTP settings.");
        }

        return "OTP sent successfully.";
    }

    // --- FORGET PASSWORD: STEP 2 (Verify & Reset) ---
    public String verifyAndResetPassword(String email, String otp, String newPassword) {
        OtpRecord record = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not requested for this email"));

        if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        if (!record.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP provided.");
        }

        // Update Password
        User user = userRepository.findByEmail(email).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Cleanup
        otpRepository.deleteByEmail(email);

        return "Password reset successful! You can now login.";
    }


}