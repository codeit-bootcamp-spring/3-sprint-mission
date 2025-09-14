package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;


    // 유저의 권한을 수정
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateUserRole(UserRoleUpdateRequest request) {
        return updateRoleInternal(request);
    }

    @Transactional
    public UserDto updateRoleInternal(UserRoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Role newRole = request.newRole();
        user.updateRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        boolean isOnline = jwtRegistry.hasActiveJwtInformationByUserId(user.getId());

        return userMapper.toDto(user, isOnline);
    }

    @Override
    public JwtDto refreshToken(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null) {
            throw new InvalidTokenException("리프레시 토큰이 없습니다.");
        }

        // 레지스트리 활성 리프레시 인지?
        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.debug("[AuthService] 레지스트리에 없는 리프레스 토큰 입니다.");
            jwtTokenProvider.expireRefreshCookie(response);
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 유효성 확인
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.debug("[AuthService] 리프레시 토큰 유효성 확인에 실패했습니다.");
            jwtTokenProvider.expireRefreshCookie(response);
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        try {
            // username 추출
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            // 사용자 로드
            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);
            UserDto userDto = userDetails.getUserDto();

            // 새 토큰 발급
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            JwtInformation newJwtInformation = new JwtInformation(
                userDto,
                newAccessToken,
                newRefreshToken
            );

            // 레지스트리 회전
            jwtRegistry.rotateJwtInformation(
                refreshToken,
                newJwtInformation
            );

            // 리프레시 쿠키 교체
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            // 응답 바디 구성
            log.debug("[AuthService] 토큰 재발급 완료 - username={}", username);
            return new JwtDto(userDto, newAccessToken);

        } catch (Exception e) {
            log.error("[AuthService] 토큰 생성 중 오류", e);
            jwtTokenProvider.expireRefreshCookie(response);
            throw new InvalidTokenException("토큰 생성 중 오류가 발생했습니다.");
        }
    }
}

