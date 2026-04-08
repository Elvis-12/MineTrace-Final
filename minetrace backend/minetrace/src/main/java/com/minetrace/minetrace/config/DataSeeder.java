package com.minetrace.minetrace.config;

import com.minetrace.minetrace.entity.Organization;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.repository.OrganizationRepository;
import com.minetrace.minetrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@minetrace.com")) {
            Organization org = new Organization();
            org.setName("MineTrace Authority");
            org.setAddress("Kigali, Rwanda");
            org.setPhone("+250 788 000 000");
            org.setEmail("info@minetrace.gov");
            Organization savedOrg = organizationRepository.save(org);

            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@minetrace.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.Status.ACTIVE);
            admin.setOrganization(savedOrg);
            userRepository.save(admin);

            log.info("==============================================");
            log.info("Default admin user created:");
            log.info("  Email:    admin@minetrace.com");
            log.info("  Password: Admin@123");
            log.info("==============================================");
        }
    }
}
