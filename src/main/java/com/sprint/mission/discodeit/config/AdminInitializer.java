package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== AdminInitializer 실행 시작 ===");

        boolean adminExists = userRepository.existsByRole(Role.ADMIN);
        log.info("ADMIN 사용자 존재 여부: {}", adminExists);

        if (!adminExists) {
            log.info("ADMIN 계정이 존재하지 않습니다. 초기 ADMIN 계정을 생성합니다.");

            // ADMIN 사용자 생성 및 저장
            User adminUser = new User(
                "admin",
                "admin@discodeit.com",
                passwordEncoder.encode("admin!"),
                null
            );
            adminUser.updateRole(Role.ADMIN);

            User savedUser = userRepository.save(adminUser);
            log.info("초기 ADMIN 계정 생성 완료 : ID = {}, username = {}",
                    savedUser.getId(), savedUser.getUsername());
        }
    }
}
