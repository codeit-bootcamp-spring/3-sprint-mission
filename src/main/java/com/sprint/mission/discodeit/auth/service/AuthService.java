package com.sprint.mission.discodeit.auth.service;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtDto refreshToken(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 refreshToken");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        DiscodeitUserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UserDto userDto = userDetails.getUserDto();

        try {
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            jwtTokenProvider.expireRefreshCookie(response);
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            log.debug("[AuthService] 토큰 재발급 완료 - username: {}", username);

            return new JwtDto(userDto, newAccessToken);

        } catch (JOSEException e) {
            log.error("[AuthService] 토큰 생성 중 오류 발생", e);
            throw new InvalidTokenException("토큰 재발급 실패");
        }
    }

    public boolean isLoggedIn(UUID userId) {
        return jwtRegistry.hasActiveJwtInformationByUserId(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public User updateRole(RoleUpdateRequest roleUpdateRequest) {
        log.debug("사용자 권한 변경 시작");

        UUID userId = roleUpdateRequest.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Role newRole = roleUpdateRequest.newRole();
        user.updateRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        log.info("사용자 권한 변경 완료");

        return user;
    }
}