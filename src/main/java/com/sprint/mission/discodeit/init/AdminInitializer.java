package com.sprint.mission.discodeit.init;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean hasAdmin = userRepository.existsByRole(Role.ADMIN);

        // ADMIN 계정이 없는 경우 초기화
        if (!hasAdmin) {
            User admin = User.builder()
                .username("admin")
                .email("admin@admin.com")
                .password(passwordEncoder.encode("admin1234"))
                .role(Role.ADMIN)
                .build();

            userRepository.save(admin);

            log.info("[AdminInitializer] ADMIN 계정 생성 완료");
        } else {
            log.info("[AdminInitializer] ADMIN 계정이 이미 있습니다.");
        }
    }
}
