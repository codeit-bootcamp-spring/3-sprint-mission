package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicAuthService implements AuthService {

    private final SessionRegistry sessionRegistry;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse updateUserRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId().toString()));

        user.updateRole(request.newRole());

        invalidateUserSessions(user.getUsername());
        log.debug("[BasicAuthService] 사용자 권한 변경 완료 및 세션 무효화 처리됨 [userId={}]", user.getId());

        return userMapper.toResponse(user);
    }


    private void invalidateUserSessions(String username) {
        try {
            log.debug("========== 세션 무효화 시작 ==========");
            log.debug("대상 사용자: {}", username);

            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            log.debug("전체 로그인된 사용자 수: {}", allPrincipals.size());

            for (Object principal : allPrincipals) {

                UserDetails userDetails = (UserDetails) principal;
                String principalName = userDetails.getUsername();

                log.debug("확인 중인 Principal: {} (username: {})", principal, principalName);

                if (username.equals(principalName)) {

                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal,
                        false);
                    log.debug("대상 사용자 발견 활성 세션 수: {}", sessions.size());

                    for (SessionInformation session : sessions) {
                        log.debug("세션 무효화 중 - 세션ID: {}", session.getSessionId());
                        session.expireNow();
                        log.debug("세션 무효화 완료 - 만료됨: {}", session.isExpired());
                    }

                    log.debug("사용자 '{}'의 모든 세션({}개)이 무효화되었습니다.", username, sessions.size());
                    break;
                }
            }

            log.debug("========== 세션 무효화 완료 ==========");

        } catch (Exception e) {
            log.error("세션 무효화 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
