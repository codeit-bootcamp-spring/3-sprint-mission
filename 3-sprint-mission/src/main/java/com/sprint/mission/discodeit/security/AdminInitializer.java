package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    @Value("${DISCODEIT_ADMIN_USERNAME}")
    private String username;

    @Value("${DISCODEIT_ADMIN_PASSWORD}")
    private String email;

    @Value("${DISCODEIT_ADMIN_EMAIL}")
    private String password;

    private final AuthService authService;
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {

        UserCreateRequest request = new UserCreateRequest(username, email, password);
        try {
            UserDto admin = userService.create(request, Optional.empty());
            // 최초 관리자 계정 생성 시 권한 수정 메서드에는 @PreAuthorize() 없어야 함
            authService.updateRoleInternal(new RoleUpdateRequest(admin.id(), Role.ADMIN));
            log.info("관리자 계정이 성공적으로 생성되었습니다.");
        } catch (UserAlreadyExistsException e) {
            log.warn("관리자 계정이 이미 존재합니다.");
        } catch (Exception e) {
            log.error("관리자 계정 생성 중 오류가 발생했습니다: {}", e.getMessage());
        }
    }
}
