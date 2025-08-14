// com.sprint.mission.discodeit.controller.AuthController
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController implements AuthApi {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(203).build();
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        return userDetails.getUserDto();
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(
            @RequestBody @Valid RoleUpdateRequest request
    ) {
        UserDto updated = userService.updateRole(request.userId(), request.newRole());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(java.util.Map.of("success", false, "message", "Invalid refresh token"));
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);
        DiscodeitUserDetails principal = (DiscodeitUserDetails) user;

        try {
            String newAccessToken = jwtTokenProvider.generateAccessToken(principal);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(principal);

            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            JwtDto body = new JwtDto(principal.getUserDto(), newAccessToken);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            return ResponseEntity.status(500)
                    .body(java.util.Map.of("success", false, "message", "Token refresh failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        jwtTokenProvider.expireRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }
}