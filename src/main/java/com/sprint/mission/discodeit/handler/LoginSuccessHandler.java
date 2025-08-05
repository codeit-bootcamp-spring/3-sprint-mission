package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.authService.LoginResponse;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.basic.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : LoginSuccessHandler
 * Author       : dounguk
 * Date         : 2025. 8. 5.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    public final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("[LoginSuccessHandler] 로그인 성공 처리 시작");
        log.info("Principal: " + authentication.getPrincipal());
        log.info("Authorities: " + authentication.getAuthorities());
        log.info("Credntials: " + authentication.getCredentials());
        log.info("Details: " + authentication.getDetails());
        log.info("isAuthenticated: " + authentication.isAuthenticated());
        log.info("Username: " + authentication.getPrincipal());

        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            User user = customUserDetails.getUser();

            BinaryContent profile = user.getProfile();
            BinaryContentResponse profileDto = null;
            if (profile != null) {
                profileDto = new BinaryContentResponse(
                    profile.getId(),
                    profile.getFileName(),
                    profile.getSize(),
                    profile.getContentType()
                );
            }

            LoginResponse loginResponse = new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profileDto,
                isOnline(user.getStatus())
            );

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            String responseBody = objectMapper.writeValueAsString(loginResponse);
            response.getWriter().write(responseBody);

            log.info("[LoginSuccessHandler] 로그인 성공 응답 완료: " + user.getUsername());

        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"인증 정보를 처리할 수 없습니다.\"}");

            log.info("[LoginSuccessHandler] 예상치 못한 Principal 타입: " + authentication.getPrincipal().getClass());
        }

    }

    private static boolean isOnline(UserStatus userStatus) {
        Instant now = Instant.now();
        return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
    }
}
