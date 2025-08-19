package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

/**
 * 세션 만료 처리 전략 기존 세션이 만료되었을 때의 처리를 담당한다. JSON 기반 응답을 반환하여 프론트엔드에서 적절히 처리할 수 있도록 한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
        throws IOException, ServletException {

        log.debug("[SessionExpiredStrategy] 세션 만료 처리 시작");

        HttpServletResponse response = event.getResponse();

        // 세션 만료 안내 메시지
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", "SESSION_EXPIRED");
        result.put("message", "현재 세션이 만료되었습니다. 다시 로그인해주세요.");

        // JSON 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
