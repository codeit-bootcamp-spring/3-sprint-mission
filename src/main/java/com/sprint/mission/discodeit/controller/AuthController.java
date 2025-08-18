// com.sprint.mission.discodeit.controller.AuthController
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final JwtRegistry jwtRegistry;


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
                    .body(Map.of("success", false, "message", "만료되었거나 무효화된 토큰입니다."));
        }

        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "만료되었거나 무효화된 토큰입니다."));
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        DiscodeitUserDetails principal =
                (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

        try {
            String newAccessToken = jwtTokenProvider.generateAccessToken(principal);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(principal);
            jwtRegistry.rotateJwtInformation(
                    refreshToken,
                    JwtInformation.builder()
                            .userDto(principal.getUserDto())
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken)
                            .build()
            );
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);
            log.info("토큰 재발급 완료: user={}, uid={}", principal.getUsername(), principal.getUserDto().id());

            return ResponseEntity.ok(new JwtDto(principal.getUserDto(), newAccessToken));

        } catch (Exception e) {
            log.error("토큰 재발급 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "토큰 재발급 실패"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        jwtTokenProvider.expireRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }
}