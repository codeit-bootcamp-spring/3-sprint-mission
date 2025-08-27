package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
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
@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final String CONFIG_NAME = "[AdminInitializer] ";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info(CONFIG_NAME + "어드민 계정 초기화 시작");

        String encodedPassword = passwordEncoder.encode(adminPassword);
        User admin = new User(adminUsername, adminEmail, encodedPassword, null);
        admin.updateRole(Role.ADMIN);

        if (userRepository.existsByUsername(adminUsername)) {
            log.info(CONFIG_NAME + "어드민 계정이 이미 존재합니다: {}", admin.getId());
            return;
        }
        userRepository.save(admin);

        log.info(CONFIG_NAME + "어드민 계정 생성 완료: username={}, email={}, role={}",
                admin.getUsername(), admin.getEmail(), admin.getRole());
    }
}
