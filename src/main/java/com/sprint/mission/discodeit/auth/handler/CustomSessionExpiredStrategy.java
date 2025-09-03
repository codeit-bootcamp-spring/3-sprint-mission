package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
        throws IOException, ServletException {

        System.out.println("[CustomSessionExpiredStrategy] 세션 만료 처리 시작");

        HttpServletResponse response = event.getResponse();

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", "SESSION_EXPIRED");
        result.put("message", "다른 곳에서 로그인되어 현재 세션이 만료되었습니다. 다시 로그인해주세요.");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}