package com.example.demo.configuration;


import com.example.demo.entity.User;
import com.example.demo.enums.Roles;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.entity.Role;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // 1. Kiểm tra nếu chưa có tài khoản admin
            if (userRepository.getUserByUsername("admin").isEmpty()) {

                // 2. Kiểm tra xem Role ADMIN đã có trong DB chưa, nếu chưa thì tạo mới
                Role adminRole = roleRepository.findById(Roles.ADMIN.name())
                        .orElseGet(() -> roleRepository.save(Role.builder()
                                .name(Roles.ADMIN.name())
                                .description("Administrator role")
                                .build()));

                HashSet<Role> roles = new HashSet<>();
                roles.add(adminRole);

                // 3. Khởi tạo tài khoản admin mặc định
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.info("Admin user has been created with default password, please change it!");
            }
        };
    }
}