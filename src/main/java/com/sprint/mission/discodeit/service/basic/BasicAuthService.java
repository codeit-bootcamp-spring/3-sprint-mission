package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.authException.UnauthorizedTokenException;
import com.sprint.mission.discodeit.exception.userException.UserNotFoundException;
import com.sprint.mission.discodeit.handler.RoleUpdatedEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       : BasicAuthService
 * author         : doungukkim date           : 2025. 4. 25. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 25.        doungukkim 최초 생성
 */

@Slf4j
@Primary
@RequiredArgsConstructor
@Service("basicAuthService")
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateRole(UserRoleUpdateRequest request) {
        return updateRoleInternal(request);
    }

    @Transactional
    @Override
    public UserDto updateRoleInternal(UserRoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        Role oldRole = user.getRole();
        Role newRole = request.newRole();
        user.changeRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(userId);
        eventPublisher.publishEvent(
            new RoleUpdatedEvent(user.getId(), oldRole, newRole, user.getUpdatedAt())
        );

        return userMapper.toDto(user);
    }

    @Override
    public JwtInformation refreshToken(String refreshToken) {
        // Validate refresh token
        if (!tokenProvider.validateRefreshToken(refreshToken)
            || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.error("Invalid or expired refresh token: {}", refreshToken);
            throw new UnauthorizedTokenException();
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            throw new UnauthorizedTokenException();
        }

        try {
            String newAccessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
            String newRefreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);
            log.info("Access token refreshed for user: {}", username);

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
            log.error("Failed to generate new tokens for user: {}", username, e);
            throw new UnauthorizedTokenException();
        }
    }
}


