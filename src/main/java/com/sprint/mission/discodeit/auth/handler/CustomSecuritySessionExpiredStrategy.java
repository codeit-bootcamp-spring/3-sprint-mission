package com.sprint.mission.discodeit.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomSecuritySessionExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
        throws IOException, ServletException {

        log.info("동일 계정이 다른 곳에서 로그인 되었습니다. 자동 로그아웃 됩니다.");

        HttpServletResponse response = event.getResponse();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String body = """
                  {
                  "code": "SESSION_EXPIRED",
                  "message": "동일 계정이 다른 곳에서 로그인 되었습니다. 자동 로그아웃 됩니다."
                  }
                  """;

        response.getWriter().write(body);
    }
}
