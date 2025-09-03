package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.dto.JwtDTO;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.store.JwtSessionRegistry;
import com.sprint.mission.discodeit.auth.jwt.store.JwtTokenEntity;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtSessionRegistry jwtSessionRegistry;
    private final UserMapper userMapper;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF token requested: {}", csrfToken.getToken());
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponse> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
        UserResponse updated = authService.updateUserRole(request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDTO> refresh(
        @CookieValue(
            name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            required = false
        )
        String refreshToken,
        HttpServletResponse response) {

        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String oldRefreshJti = jwtTokenProvider.getTokenId(refreshToken);

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(
            username);

        try {
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            String newRefreshJti = jwtTokenProvider.getTokenId(newRefreshToken);

            if (jwtSessionRegistry.isRevoked(oldRefreshJti)) {
                jwtTokenProvider.expireRefreshCookie(response);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            jwtSessionRegistry.markReplaced(oldRefreshJti, newRefreshJti);

            JwtTokenEntity accessEntity = jwtTokenProvider.toEntity(newAccessToken);
            JwtTokenEntity refreshEntity = jwtTokenProvider.toEntity(newRefreshToken);
            jwtSessionRegistry.register(accessEntity);
            jwtSessionRegistry.register(refreshEntity);

            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            User user = userDetails.getUser();
            UserResponse userResponse = userMapper.toResponse(userDetails.getUser());
            JwtDTO body = new JwtDTO(userResponse, newAccessToken);

            return ResponseEntity.ok(body);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
