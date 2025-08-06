package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : CustomAccessDeniedHandler
 * Author       : dounguk
 * Date         : 2025. 8. 6.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("[CustomAccessDeniedHandler] 접근 거부");
        log.warn("[CustomAccessDeniedHandler] 요청 URL: {}", request.getRequestURI());
        log.warn("[CustomAccessDeniedHandler] 접근 거부 사유: {}", accessDeniedException.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code("403")
            .message("해당 리소스에 접근할 권한이 없습니다.")
            .exceptionType(accessDeniedException.getClass().getSimpleName())
            .status(HttpStatus.FORBIDDEN.value())
            .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403

        // JSON 응답 전송
        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(responseBody);
    }
}
