package com.sprint.mission.discodeit.service;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));
        user.updateRole(request.newRole());

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        return userMapper.toDto(user);
    }

    public JwtInformation reissueTokens(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)
                || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.error("유효하지 않거나 만료된 리프레시 토큰입니다. 토큰: {}", refreshToken);
            throw new DiscodeitException(ErrorCode.INVALID_JWT_TOKEN);
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            throw new DiscodeitException(ErrorCode.INVALID_USER_DETAILS);
        }

        try {
            String newAccessToken = tokenProvider.createAccessToken(discodeitUserDetails);
            String newRefreshToken = tokenProvider.createRefreshToken(discodeitUserDetails);

            JwtInformation newJwtInformation = new JwtInformation(
                    discodeitUserDetails.getUserDto(),
                    newAccessToken,
                    newRefreshToken
            );
            jwtRegistry.rotateJwtInformation(
                    refreshToken,
                    newJwtInformation
            );

            return newJwtInformation;

        } catch (JOSEException e) {
            log.error("액세스 토큰 재발급에 실패했습니다.", e);
            throw new DiscodeitException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

}