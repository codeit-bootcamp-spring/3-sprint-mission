package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.countByRole(Role.ADMIN) == 0) {
            log.info("초기 어드민 계정을 생성합니다.");

            User admin = new User(
                "admin",
                "admin@discodeit.com",
                passwordEncoder.encode("admin123!"),
                null // 프로필 없음
            );
            admin.updateRole(Role.ADMIN);
            userRepository.save(admin);

            log.info("초기 어드민 계정 생성 완료: username=admin");
        }
    }
}