package com.buildingmaintenancesystem.config;

import com.buildingmaintenancesystem.entity.Role;
import com.buildingmaintenancesystem.entity.User;
import com.buildingmaintenancesystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an Admin already exists
        if (!userRepository.existsByRole(Role.ROLE_ADMIN)) {
            User masterAdmin = new User();
            masterAdmin.setFullName("Master Admin");
            masterAdmin.setUsername("admin");
            masterAdmin.setEmail("admin@society.com");
            masterAdmin.setPassword(passwordEncoder.encode("admin123")); // Default password
            masterAdmin.setRole(Role.ROLE_ADMIN); // 👈 This is the only way to get an ADMIN role now!

            userRepository.save(masterAdmin);
            System.out.println("✅ Master Admin created successfully! (Username: admin, Pass: admin123)");
        }
    }
}