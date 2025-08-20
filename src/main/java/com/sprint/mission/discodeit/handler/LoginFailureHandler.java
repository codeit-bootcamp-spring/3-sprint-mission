package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        System.out.println("[LoginFailureHandler] 로그인 실패 처리 시작");
        System.out.println("[LoginFailureHandler] 실패 사유: " + exception.getClass().getSimpleName());

        // 에러 응답 생성
        ErrorResponse errorResponse = determineError(exception);

        // 응답 헤더 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // JSON 응답 전송
        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(responseBody);

        System.out.println("[LoginFailureHandler] 로그인 실패 처리 완료");
    }

    /*예외 타입에 따른 에러  결정*/
    private ErrorResponse determineError(AuthenticationException exception) {
        ErrorCode errorCode;
        if (exception instanceof BadCredentialsException) {
            errorCode = ErrorCode.INVALID_CREDENTIALS;
        } else if (exception instanceof DisabledException) {
            errorCode = ErrorCode.ACCOUNT_DISABLED;
        } else {
            errorCode = ErrorCode.AUTHENTICATION_ERROR;
        }

        return ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(errorCode.getCode())
            .message(errorCode.getDefaultMessage())
            .status(errorCode.getHttpStatus())
            .build();
    }
}
