package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 관리자 계정 초기화 클래스
 * <p>
 * 관리자 계정이 존재하지 않는다면 애플리케이션이 실행될 때,
 * 관리자 계정이 추가된다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(Role.ADMIN)){
            log.info("[AdminAccountInitializer] Admin 계정이 존재하지 않습니다. 생성을 시작합니다.");

            UserCreateRequest request = new UserCreateRequest(
                "관리자",
                "admin@test.com",
                "asdf1234"
            );
            userService.create(request, Optional.empty());

            User user = userRepository.findByUsername("관리자")
                    .orElseThrow( () -> new UsernameNotFoundException("관리자"));

            user.updateRole(Role.ADMIN);
            userRepository.save(user);

            log.info("[AdminAccountInitializer] Admin 계정 초기화 완료.");
        }
    }
}