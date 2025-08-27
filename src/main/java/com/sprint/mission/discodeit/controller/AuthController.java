package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;


/**
 * packageName    : com.sprint.mission.discodeit.controller fileName       : AuthController author
 * : doungukkim date           : 2025. 5. 10. description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 5. 10.        doungukkim
 * 최초 생성
 */

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        System.out.println("Csrf token 요청: "+ tokenValue);
        System.out.println("파라미터 이름: " + csrfToken.getParameterName());
        System.out.println("헤더 이름: " + csrfToken.getHeaderName());
        System.out.println("토큰 값: " + csrfToken.getToken());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequest request){
        return ResponseEntity.ok(userService.updateRole(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken,
                                          HttpServletResponse response) {
        JwtInformation jwtInformation = authService.refreshToken(refreshToken);
        Cookie refreshCookie = jwtTokenProvider.genereateRefreshTokenCookie(
            jwtInformation.getRefreshToken());
        response.addCookie(refreshCookie);

        JwtDto body = new JwtDto(
            jwtInformation.getUserDto(),
            jwtInformation.getAccessToken()
        );
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(body);
    }
}
