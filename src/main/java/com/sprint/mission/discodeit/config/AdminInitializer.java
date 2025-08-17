package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${discodeit.admin.username:admin}")
    private String adminUsername;

    @Value("${discodeit.admin.email:admin@discodeit.local}")
    private String adminEmail;

    @Value("${discodeit.admin.password:admin1234!}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args)  {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("ADMIN 계정 이미 존재함. 초기화 건너뜀.");
            return;
        }

        if (userRepository.existsByUsername(adminUsername) || userRepository.existsByEmail(adminEmail)) {
            log.error("ADMIN 초기화 실패 - username/email 이미 사용 중.");
            return;
        }

        User admin = new User(
            adminUsername,
            adminEmail,
            passwordEncoder.encode(adminPassword),
            null
        );
        admin.updateRole(Role.ADMIN);


        userRepository.save(admin);
        log.warn("기본 ADMIN 계정 생성 완료 - username={}, email={}", adminUsername, adminEmail);
    }
}
