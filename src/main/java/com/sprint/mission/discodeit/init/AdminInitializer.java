package com.sprint.mission.discodeit.init;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdminAccount() {
        boolean exists = userRepository.existsByRole(Role.ADMIN);

        if (!exists) {
            User admin = User.builder()
                .username("admin")
                .email("admin@discodiet.com")
                .password(passwordEncoder.encode("admin1234"))
                .role(Role.ADMIN)
                .build();

            userRepository.save(admin);
            log.info(
                "[AdminInitializer] ADMIN account has been initialized. [username=admin, email=admin@discodiet.com]");
        } else {
            log.info("[AdminInitializer] ADMIN account already exists. Initialization skipped.");
        }
    }
}
