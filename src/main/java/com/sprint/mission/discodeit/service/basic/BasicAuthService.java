package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.jwt.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtRegistry jwtRegistry;

    public JwtDto refresh(String refreshToken, HttpServletResponse response) {

        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken) ||
            !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.error("유효하지 않는 RefreshToken: {}", refreshToken);
            throw new InvalidTokenException(refreshToken);
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(
            username);

        JwtDto jwtDto = null;
        try {
            // 새 토큰 발급
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            JwtInformation newJwtInfo = new JwtInformation(userDetails.getId(),
                userDetails.getUsername(),
                newAccessToken, newRefreshToken);

            // Refresh 토큰 Rotation
            JwtInformation jwtInformation = jwtRegistry.rotateJwtInformation(refreshToken,
                newJwtInfo);

            // 리프레시 쿠키 교체
            // HTTP 응답 헤더(Set-Cookie)에 Refresh Cookie를 추가한다.
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            User user = findUser(userDetails.getId());
            UserResponseDto userDto = userMapper.toDto(user);

            jwtDto = new JwtDto(userDto, jwtInformation.accessToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jwtDto;
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundUserException(id));
    }
}
