package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.jwt.store.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 사용자 인증 및 권한 관리를 담당하는 서비스 구현체입니다.
 * 
 * <p>JWT 기반 인증 시스템에서 사용자의 로그인 상태 확인, 권한 변경, 
 * 토큰 무효화 등의 기능을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 권한 변경</li>
 *   <li>로그인 상태 확인</li>
 *   <li>권한 변경 시 토큰 무효화</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 * @see AuthService
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final InMemoryJwtRegistry jwtRegistry;

    private static final String SERVICE_NAME = "[AuthService] ";

    /**
     * 사용자의 권한을 변경합니다.
     * 
     * <p>관리자 권한이 있는 사용자만 호출할 수 있으며, 권한 변경 시 
     * 해당 사용자가 로그인 상태라면 모든 JWT 토큰을 무효화합니다.</p>
     * 
     * @param request 권한 변경 요청 정보
     * @return 변경된 사용자 정보
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));


        user.updateRole(request.newRole());

        UUID userId = user.getId();

        if (isOnline(userId)) {
            jwtRegistry.invalidateJwtInformationByUserId(userId);
            log.info(SERVICE_NAME + "사용자 권한 변경 및 세션 무효화 완료 ");
        } else {
            log.info(SERVICE_NAME + "사용자 권한 변경 완료 (로그인 상태 아님): userId={}", userId);
        }


        return userMapper.toDto(user);
    }

    /**
     * 사용자의 현재 로그인 상태를 확인합니다.
     * 
     * @param userId 확인할 사용자의 ID
     * @return 로그인 상태인 경우 true, 그렇지 않으면 false
     */
    private boolean isOnline(UUID userId) {
        return jwtRegistry.hasActiveJwtInformationByUserId(userId);
    }
}
