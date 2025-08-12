package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    private static final String SERVICE_NAME = "[AuthService] ";

    @Override
    @Transactional
    public UserDto getCurrentUserInfo(DiscodeitUserDetails userDetails) {

        log.debug(SERVICE_NAME + "현재 사용자 정보 조회 요청");

        UUID userId = userDetails.getUserDto().id();
        log.debug(SERVICE_NAME + "조회할 사용자 ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        UserDto userDto = userMapper.toDto(user);
        log.debug(SERVICE_NAME + "DTO 변환 완료: {}", userDto);

        boolean online = isOnline(userDto.id());

        log.debug(SERVICE_NAME + "온라인 상태 계산 완료: username={}, online={}", userDto.username(), online);

        return new UserDto(
                userDto.id(),
                userDto.username(),
                userDto.email(),
                userDto.profile(),
                online,
                userDto.role()
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

        user.updateRole(request.newRole());
        invalidateUserSessionsByUsername(user.getUsername());

        log.info(SERVICE_NAME + "사용자 권한 변경 및 세션 무효화 완료 ");

        return userMapper.toDto(user);
    }

    private void invalidateUserSessionsByUsername(String username) {
        try {
            int expiredCount = 0;

            for (Object principal : sessionRegistry.getAllPrincipals()) {

                String principalUsername = getPrincipalUsername(principal);

                if (principalUsername != null && principalUsername.equals(username)) {
                    var sessions = sessionRegistry.getAllSessions(principal, false);
                    if (!sessions.isEmpty()) {
                        sessions.forEach(SessionInformation::expireNow);
                        expiredCount += sessions.size();
                        log.info(SERVICE_NAME + "사용자 '{}'의 세션 {}개를 무효화했습니다.", username, sessions.size());
                    }
                }
            }

            if (expiredCount > 0) {
                log.info(SERVICE_NAME + "사용자 '{}'의 만료된 세션 수: {}", username, expiredCount);
            } else {
                log.info(SERVICE_NAME + "사용자 '{}'의 활성 세션이 없습니다.", username);
            }
        } catch (Exception e) {
            log.error(SERVICE_NAME + "사용자 '{}'의 세션 무효화 중 오류: {}", username, e.getMessage(), e);
        }
    }

    private boolean isOnline(UUID userId) {
        if (userId == null) {
            return false;
        }

        return sessionRegistry.getAllPrincipals().stream()
                .anyMatch(principal -> {
                    if (principal instanceof DiscodeitUserDetails userDetails) {
                        boolean sameUser = userId.equals(userDetails.getUserDto().id());
                        if (!sameUser) return false;
                        return !sessionRegistry.getAllSessions(principal, false).isEmpty();
                    }

                    return false;
        });
    }

    private String getPrincipalUsername(Object principal) {
        if (principal instanceof DiscodeitUserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return user.getUsername();
        } else if (principal instanceof String name) {
            return name;
        }

        log.warn(SERVICE_NAME + "예상치 못한 principal 타입: {}",
                principal != null ? principal.getClass().getName() : "null");

        return null;
    }
}
