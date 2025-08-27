package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 접근 거부 핸들러
 * <p>
 * 인증은 되었지만 권한이 부족한 경우 403 응답을 JSON으로 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String userId = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName()
            : "anonymous";

        log.info("[AccessDenied] 접근 거부 발생 - 요청 URL: {}, 사유: {}, userId={}",
            request.getRequestURI(),
            accessDeniedException.getMessage(),
            userId);

        if (log.isDebugEnabled()) {
            log.debug("[AccessDenied] 상세 정보 - Remote IP: {}, 세션 ID: {}",
                request.getRemoteAddr(),
                request.getSession(false) != null ? request.getSession(false).getId()
                    : "no-session");
        }

        // JSON 에러 응답 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", "ACCESS_DENIED");
        errorResponse.put("message", "해당 리소스에 접근할 권한이 없습니다.");
        errorResponse.put("status", 403);

        // 응답 헤더 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403

        // JSON 응답 전송
        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(responseBody);

        log.info("[AccessDenied] 접근 거부 응답 완료 - userId={}", userId);
    }
}
