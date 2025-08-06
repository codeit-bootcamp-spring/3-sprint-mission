package com.sprint.mission.discodeit.helper;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * PackageName  : com.sprint.mission.discodeit.helper
 * FileName     : AdminInitializer
 * Author       : dounguk
 * Date         : 2025. 8. 6.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String EMAIL = "admin@admin.com";
    private static final Role ROLE = Role.ADMIN;


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("ADMIN 계정 생성");
            User user = User.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .role(ROLE)
                .build();
            userRepository.save(user);
        }
    }
}
