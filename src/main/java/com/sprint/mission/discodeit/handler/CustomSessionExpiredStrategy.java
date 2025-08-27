package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;
import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : CustomSessionExpiredStrategy
 * Author       : dounguk
 * Date         : 2025. 8. 6.
 */

public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    private final ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        System.out.println("[CustomSessionExpiredStrategy] 세션 만료 처리");

        HttpServletResponse response = event.getResponse();

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code("401")
            .message("다른 곳에서 로그인되어 현재 세션이 만료되었습니다. 다시 로그인해주세요.")
            .status(HttpStatus.UNAUTHORIZED.value())
            .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(responseBody);
    }
}
