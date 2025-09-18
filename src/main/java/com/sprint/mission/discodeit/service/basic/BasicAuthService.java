package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    @Override
    public UserDto getCurrentUserInfo(UserDetails userDetails) {

        log.debug("[AuthService] 현재 사용자 정보 조회 요청");

        if (userDetails == null) {
            log.debug("[AuthService] UserDetails가 null 입니다.");
            return null;
        }

        String username = userDetails.getUsername();
        log.debug("[AuthService] username: {}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        log.debug("[AuthService] user: {}", user);

        UserDto userResponse = userMapper.toDto(user);

        boolean online = !sessionRegistry.getAllSessions(userDetails, false).isEmpty();

        log.debug("[AuthService] userDto: {}", userResponse);

        return new UserDto(
            userResponse.id(),
            userResponse.username(),
            userResponse.email(),
            userResponse.profile(),
            online,
            userResponse.role()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateUserRole(UserRoleUpdateRequest userRoleUpdateRequest) {

        UUID userId = userRoleUpdateRequest.userId();
        Role newRole = userRoleUpdateRequest.newRole();

        log.info("사용자 권한 변경 시작");

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        String username = user.getUsername();
        Role oldRole = user.getRole();

        log.info("사용자: {}", username);
        log.info("기존 권한: {}, 새 권한: {}", oldRole, newRole);

        if (oldRole != newRole) {
            log.info("권한이 변경되었으므로 해당 사용자의 모든 세션을 무효화합니다.");
            invalidateUserSessions(username);
        }

        user.updateRole(newRole);
        User updatedUser = userRepository.save(user);

        log.info("사용자 권한 변경 완료");

        return userMapper.toDto(updatedUser);
    }

    @Override
    public void invalidateUserSessions(String username) {

        try {

            log.info("[UserService] 세션 무효화 시작");
            log.info("[UserService] 대상 사용자: {}", username);

            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            log.info("[UserService] 전체 로그인된 사용자 수: {}", allPrincipals.size());

            for (Object principal : allPrincipals) {
                UserDetails userDetails = (UserDetails) principal;
                String principalName = userDetails.getUsername();

                log.info("[UserService] 확인 중인 Principal: {} (username: {})", principal,
                    principalName);

                if (username.equals(principalName)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal,
                        false);
                    log.info("[UserService] 대상 사용자 발견! 활성 세션 수: {}", sessions.size());

                    for (SessionInformation session : sessions) {
                        log.info("[UserService] 세션 무효화 중 - 세션ID: {}", session.getSessionId());
                        session.expireNow();
                        log.info("[UserService] 세션 무효화 완료 - 만료됨: {}", session.isExpired());
                    }

                    log.info("[UserService] 사용자 '{}'의 모든 세션({}개)이 무효화되었습니다.", username,
                        sessions.size());
                    break;
                }
            }

            log.info("[UserService] 세션 무효화 완료");
        } catch (Exception e) {
            log.error("[UserService] 세션 무효화 중 오류 발생: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
