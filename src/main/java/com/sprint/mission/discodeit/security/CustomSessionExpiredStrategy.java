package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * 세션 만료 처리 전략
 * <p>
 * 동시 로그인 제한으로 인해 기존 세션이 만료되었을 때의 처리를 담당한다. JSON 기반 응답을 반환하여 프론트엔드에서 적절히 처리할 수 있도록 한다.
 */
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
        throws IOException, ServletException {

        System.out.println("[CustomSessionExpiredStrategy] 세션 만료 처리 시작");

        HttpServletResponse response = event.getResponse();

        // 세션 만료 안내 메시지 작성
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", "SESSION_EXPIRED");
        result.put("message", "다른 곳에서 로그인되어 현재 세션이 만료되었습니다. 다시 로그인해주세요.");

        // JSON 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}