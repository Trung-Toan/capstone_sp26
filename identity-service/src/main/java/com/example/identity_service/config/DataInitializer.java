package com.example.identity_service.config;

import com.example.identity_service.entity.AuthProvider;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create only USER and ADMIN roles
        createRoleIfNotExists("USER", "Basic user role");
        createRoleIfNotExists("ADMIN", "Administrator role");

        createAdminUserIfNotExists();

        logger.info("Database initialization completed");
    }

    private void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .description(description)
                    .build();
            roleRepository.save(role);
            logger.info("Created role: {}", roleName);
        }
    }

    private void createAdminUserIfNotExists() {
        if (!userRepository.existsByUserName("admin")) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .userName("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@identity-service.com")
                    .provider(AuthProvider.LOCAL)
                    .roles(roles)
                    .build();

            userRepository.save(admin);
            logger.info("Created default admin user (username: admin, password: admin123)");
        }
    }
}
