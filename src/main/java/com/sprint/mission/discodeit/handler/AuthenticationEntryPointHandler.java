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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        System.out.println("[AuthenticationEntryPointHandler] 로그인되지않는 사용자가 요청");

        // 응답 헤더 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // JSON 응답 전송
        String responseBody = objectMapper.writeValueAsString(
            ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(ErrorCode.UNAUTHORIZED_REQUEST.getCode())
                .message(ErrorCode.UNAUTHORIZED_REQUEST.getDefaultMessage())
                .status(ErrorCode.UNAUTHORIZED_REQUEST.getHttpStatus())
                .build()
        );
        response.getWriter().write(responseBody);
    }
}
