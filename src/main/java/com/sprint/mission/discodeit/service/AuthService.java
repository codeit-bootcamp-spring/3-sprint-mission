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
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;
    private final JwtTokenProvider tokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));
        user.updateRole(request.newRole());

        String username = user.getUsername();
        invalidateUserSessions(username);

        return userMapper.toDto(user);
    }

    public TokenDto reissueTokens(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            log.error("유효하지 않거나 만료된 리프레시 토큰입니다. 토큰: {}", refreshToken);
            throw new DiscodeitException(ErrorCode.INVALID_JWT_TOKEN);
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

        try {
            String newAccessToken = tokenProvider.createAccessToken(userDetails);
            String newRefreshToken = tokenProvider.createRefreshToken(userDetails);

            return new TokenDto(
                    userDetails.getUserDto(),
                    newAccessToken,
                    newRefreshToken
            );

        } catch (JOSEException e) {
            log.error("액세스 토큰 재발급에 실패했습니다.", e);
            throw new DiscodeitException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    private void invalidateUserSessions(String username) {

        sessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof UserDetails)
                .map(p -> (UserDetails) p)
                .filter(userDetails -> username.equals(userDetails.getUsername()))
                .findFirst().ifPresent(principal -> sessionRegistry.getAllSessions(principal, false)
                        .forEach(SessionInformation::expireNow));

    }

}