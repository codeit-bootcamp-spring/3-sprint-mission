package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : LoginFailureHandler
 * Author       : dounguk
 * Date         : 2025. 8. 5.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("[LoginFailureHandler] 로그인 실패 처리 시작");
        log.error("[LoginFailureHandler] 실패 사유: {}", exception.getClass().getSimpleName());

        String errorMessage = determineErrorMessage(exception);

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code("401")
            .message(errorMessage)
            .exceptionType(exception.getClass().getSimpleName())
            .status(HttpStatus.UNAUTHORIZED.value())
            .build();


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(responseBody);
    }

    private String determineErrorMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return "아이디 또는 비밀번호가 올바르지 않습니다.";
        } else if (exception instanceof DisabledException) {
            return "비활성화된 계정입니다.";
        } else {
            return "로그인에 실패했습니다.";
        }
    }
}
