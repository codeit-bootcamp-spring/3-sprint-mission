package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 실행 시 관리자 계정을 초기화하는 클래스.
 *
 * <p>
 * 설정값(discodeit.admin.*)을 기반으로 관리자 계정을 생성하며,
 * 이미 존재할 경우 생성 과정을 건너뛴다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    @Value("${discodeit.admin.username}")
    private String username;

    @Value("${discodeit.admin.password}")
    private String password;

    @Value("${discodeit.admin.email}")
    private String email;

    private final UserService userService;
    private final AuthService authService;

    /**
     * 애플리케이션 시작 시 실행되어 관리자 계정을 생성한다.
     *
     * @param args 애플리케이션 인자
     */
    @Override
    public void run(ApplicationArguments args)  {
        UserCreateRequest request = new UserCreateRequest(
            username,
            email,
            password
        );

        try {
            // 관리자 계정 생성 및 ADMIN 권한 부여
            UserDto admin = userService.create(request, Optional.empty());
            authService.updateRoleInternal(new RoleUpdateRequest(admin.id(), Role.ADMIN));
            log.info("관리자 계정이 성공적으로 생성되었습니다.");
        } catch (UserAlreadyExistsException e) {
            log.warn("관리자 계정이 이미 존재합니다");
        } catch (Exception e) {
            log.error("관리자 계정 생성 중 오류가 발생했습니다.: {}", e.getMessage());
        }
    }
}

