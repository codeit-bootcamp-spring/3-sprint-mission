package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.util.OnlineStatusUtil;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;
    private final OnlineStatusUtil onlineStatusUtil;

    // 유저의 권한을 수정
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUserRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));

        Role oldRole = user.getRole();

        log.info("[BasicAuthService] {} 님의 권한 변경을 시도합니다.", user.getUsername());

        if (!oldRole.equals(request.newRole())){
            user.updateRole(request.newRole());
            User updatedUser = userRepository.save(user);

            // 권한이 변경된 유저의 세션을 무효화 하기
            invalidateUserSessions(user.getUsername());
            log.info("[BasicAuthService] {} 님의 모든 세션을 무효화하고 권한을 변경했습니다.", user.getUsername());

            return userMapper.toDto(updatedUser, false);
        }

        log.info("[BasicAuthService] 기존 권한과 동일합니다.");
        boolean isOnline = onlineStatusUtil.isOnlineUser(user.getId());
        return userMapper.toDto(user, isOnline);
    }

    // 수정된 유저의 세션을 무효화
    private void invalidateUserSessions(String username) {
        try {
            log.info("[BasicAuthService] {} 님의 세션 무효화 시작.", username);

            // SessionRegistry에서 모든 주체(Principal) 조회
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            log.info("[UserService] 전체 로그인된 사용자 수: {} ", allPrincipals.size());

            // 해당 사용자의 모든 세션 정보 찾기
            for (Object principal : allPrincipals) {
                if (principal instanceof UserDetails userDetails) {
                    String principalName = userDetails.getUsername();
                    log.info("[BasicAuthService] 확인중인 사용자: {}", principalName);

                    if (username.equals(principalName)) {
                        // 해당 사용자의 모든 세션 정보 가져오기
                        List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(
                            principal, false);
                        log.info("[BasicAuthService] 해당 사용자의 활성 세션 수: {}", sessionInformations.size());

                        // 모든 세션 무효화
                        for (SessionInformation session : sessionInformations) {
                            session.expireNow();
                            log.debug("[BasicAuthService] 세션 만료 처리 완료 - sessionId={}", session.getSessionId());
                        }
                        break;
                    }
                } else {
                    // principal 타입이 UserDetails가 아닐 수 있으므로디버그 로깅
                    log.debug("[BasicAuthService] UserDetails가 아닌 principal 발견: {}", principal.getClass().getName());
                }
            }
        }
        catch (Exception e) {
            log.error("[BasicAuthService] 세션 무효화 중 오류 발생: {}", e.getMessage());
            log.error("[BasicAuthService] 오류 설명 메시지", e);
            // 세션 무효화 실패는 권한 변경 자체를 실패시키지 않음 (DB 변경은 유지)
        }
    }

}

