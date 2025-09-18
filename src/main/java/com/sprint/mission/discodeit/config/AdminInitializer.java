package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminAccount();
    }

    private void initializeAdminAccount() {

        if (userRepository.existsByRole(Role.ADMIN)) {
            log.debug("[AdminInitializer] ADMIN 계정이 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("[AdminInitializer] ADMIN 계정이 없습니다. 기본 관리자 계정을 생성합니다.");

        String adminUsername = "Admin";
        String adminEmail = "admin@discodeit.com";
        String adminPassword = passwordEncoder.encode("!qwe123");

        User admin = new User(
            adminUsername,
            adminEmail,
            adminPassword,
            null,
            Role.ADMIN
        );
        userRepository.save(admin);

        log.info("[AdminInitializer] 기본 관리자 계정이 생성되었습니다. username: {}", adminUsername);
    }
}
