package com.sprint.mission.discodeit.security.initializer;

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
@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${discodeit.admin.username}")
    private String username;
    @Value("${discodeit.admin.password}")
    private String password;
    @Value("${discodeit.admin.email}")
    private String email;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        log.info("기본 Admin 계정 생성 요청");

        userRepository.findByEmail(email)
            .ifPresentOrElse(
                existing -> {
                    existing.updateRole(Role.ADMIN);
                    userRepository.save(existing);
                    log.info("기존 유저를 Admin으로 승격: {}", email);
                },
                () -> {
                    User user = new User(username, email, passwordEncoder.encode(password), null);
                    user.updateRole(Role.ADMIN);
                    userRepository.save(user);
                    log.info("새 기본 Admin 계정 생성 완료");
                }
            );
    }

}
