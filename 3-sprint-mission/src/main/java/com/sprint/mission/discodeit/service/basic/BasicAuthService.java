package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;
    private final JwtTokenProvider tokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateUserRole(RoleUpdateRequest request) {
        return updateRoleInternal(request);
    }

    @Transactional
    @Override
    public UserDto updateRoleInternal(RoleUpdateRequest request) {
        UUID userId = request.userId();
        Role newRole = request.newRole();

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateRole(newRole);
        User updatedUser = userRepository.save(user);

        // 권한 변경 후 해당 사용자 세션 강제 만료
        jwtRegistry.invalidateJwtInformationByUserId(userId);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public JwtInformation refreshToken(String refreshToken) {

        if (!tokenProvider.validateRefreshToken(refreshToken)
                || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            throw new DiscodeitException(ErrorCode.INVALID_USER_DETAILS);
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

            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

            return newJwtInformation;

        } catch (JOSEException e) {

            log.error("Failed to generate new tokens for user: {}", username, e);
            throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    public void expireUserSessions(UUID userId) {
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            if (principal instanceof DiscodeitUserDetails usreDetails &&
                    usreDetails.getUserDto().id().equals(userId)) {

                sessionRegistry.getAllSessions(principal, false)
                        .forEach(sessionInfo -> sessionInfo.expireNow());
            }
        });
    }
}
