package com.bezkoder.springjwt.config;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoleDataInitializer.class);

    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        seedRole(ERole.ROLE_USER);
        seedRole(ERole.ROLE_MODERATOR);
        seedRole(ERole.ROLE_ADMIN);
    }

    private void seedRole(ERole roleName) {
        if (roleRepository.findByName(roleName).isPresent()) {
            return;
        }

        roleRepository.save(new Role(roleName));
        logger.info("Seeded missing role: {}", roleName);
    }
}
