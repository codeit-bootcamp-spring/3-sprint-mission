package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Admin 계정 초기화 작업 시작");

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin 계정이 이미 존재하기 때문에 초기화하지 않고 종료");
            return;
        }

        User admin = User.withAdminRole(
            "admin",
            adminEmail,
            passwordEncoder.encode(adminPassword)
        );

        userRepository.save(admin);
        log.info("Admin 계정 초기화 작업 완료");
    }
}