package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String HANDLER_NAME = "[CustomAccessDeniedHandler] ";
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.info(HANDLER_NAME + "접근 거부 처리 시작");
        log.info(HANDLER_NAME + "요청 URL: " + request.getRequestURL().toString());
        log.info(HANDLER_NAME + "접근 거부 사유: " + accessDeniedException.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", "ACCESS_DENIED");
        errorResponse.put("message", "해당 리소스에 접근할 권한이 없습니다.");
        errorResponse.put("status", 403);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(responseBody);

        log.info(HANDLER_NAME + "접근 거부 응답 완료");
    }
}
