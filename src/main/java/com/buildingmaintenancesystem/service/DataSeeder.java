package com.buildingmaintenancesystem.service;



import com.buildingmaintenancesystem.entity.*;
import com.buildingmaintenancesystem.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FlatRepository flatRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeeder(UserRepository userRepository, FlatRepository flatRepository) {
        this.userRepository = userRepository;
        this.flatRepository = flatRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Agar pehle se data hai toh dobara nahi dalega
        if (userRepository.count() > 5) return;

        System.out.println("🚀 Seeding 300 users and flats for presentation...");

        List<User> users = new ArrayList<>();
        List<Flat> flats = new ArrayList<>();

        for (int i = 1; i <= 300; i++) {
            // 1. Create User
            User user = new User();
            user.setUsername("user" + i);
            user.setFullName("Resident Name " + i);
            user.setEmail("user" + i + "@society.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(Role.ROLE_USER);
            users.add(user);

            // 2. Create Flat
            Flat flat = new Flat();
            flat.setFlatNumber(String.valueOf(100 + i)); // 101, 102...
            flat.setWing(i <= 150 ? "A" : "B");
            flat.setFloor(String.valueOf((i / 10) + 1));
            flat.setStatus(OccupancyStatus.SELF_OCCUPIED);
            flat.setOwner(user);
            flats.add(flat);
        }

        userRepository.saveAll(users);
        flatRepository.saveAll(flats);

        System.out.println("✅ Seeding Complete! 300 Users & Flats added.");
    }
}
