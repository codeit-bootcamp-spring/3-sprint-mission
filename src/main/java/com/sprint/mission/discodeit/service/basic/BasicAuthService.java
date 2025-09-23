package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

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

    @Override
    public JwtDto refreshToken(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 refreshToken입니다");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        DiscodeitUserDetails userDetails =
            (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

        UserDto userDto = userDetails.getUserDto();

        try {
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            jwtTokenProvider.expireRefreshCookie(response);
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            log.debug("[AuthService] 토큰 재발급 완료 - username: {}", username);

            JwtInformation newInfo = new JwtInformation(userDto, newAccessToken, newRefreshToken);

            jwtRegistry.rotateJwtInformation(refreshToken, newInfo);
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            return JwtDto.of(userDto, newAccessToken);

        } catch (JOSEException e) {
            log.error("[AuthService] 토큰 생성 중 오류 발생", e);
            throw new InvalidTokenException("토큰 생성 중 오류가 발생했습니다");

        }
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
            log.info("권한이 변경되었으므로 해당 사용자의 모든 토큰을 무효화합니다.");
            jwtRegistry.invalidateJwtInformationByUserId(userId);
        }

        user.updateRole(newRole);
        User updatedUser = userRepository.save(user);

        eventPublisher.publishEvent(
            new RoleUpdatedEvent(userId, oldRole, newRole, Instant.now())
        );

        log.info("사용자 권한 변경 완료");

        return userMapper.toDto(updatedUser);
    }

}
