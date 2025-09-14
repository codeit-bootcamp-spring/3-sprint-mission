package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.auth.CustomAccessDeniedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 상세한 권한 거부 로그
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        log.error("===== ACCESS DENIED =====");
        log.error("요청 URL: {} {}", request.getMethod(), request.getRequestURI());
        log.error("요청 파라미터: {}", request.getQueryString());
        
        if (authentication != null) {
            log.error("현재 사용자: {}", authentication.getName());
            log.error("현재 권한: {}", authentication.getAuthorities());
            log.error("인증 상태: {}", authentication.isAuthenticated());
        } else {
            log.error("인증 정보 없음");
        }
        
        log.error("접근 거부 사유: {}", accessDeniedException.getMessage());
        log.error("========================");

        CustomAccessDeniedException e = new CustomAccessDeniedException();

        // ErrorResponse 객체 생성
        ErrorResponse errorResponse = ErrorResponse.of(e);

        response.setStatus(e.getErrorCode().getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE+ ";charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
