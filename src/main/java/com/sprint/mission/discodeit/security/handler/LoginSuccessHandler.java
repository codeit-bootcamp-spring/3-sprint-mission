package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        log.debug("[LoginSuccessHandler] 로그인 성공 처리 시작");

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            // 로그인 성공 시 사용자 정보 응답
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), userDetails.getUserDto());

            log.debug("[LoginSuccessHandler] 로그인 성공 응답 완료: 사용자명={}", userDetails.getUsername());
        } else {
            // 예상치 못한 Principal 타입인 경우
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"인증 정보를 처리할 수 없습니다.\"}");

            log.debug("[LoginSuccessHandler] 예상치 못한 Principal 타입: " + authentication.getPrincipal().getClass());
        }
    }
}
