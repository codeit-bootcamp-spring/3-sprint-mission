package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.response.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        log.info("[LoginSuccessHandler] 로그인 성공 처리 시작");

        // UserDetails에서 사용자 정보 추출
        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            UserDto userDto = userDetails.getUserDto();

            // JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            // 사용자 정보를 JSON으로 응답
            String responseBody = objectMapper.writeValueAsString(userDto);
            response.getWriter().write(responseBody);

            log.info("[LoginSuccessHandler] 로그인 성공 응답 완료: {}", userDto.username());
        } else {
            // 예상치 못한 Principal 타입인 경우
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("[onAuthenticationSuccess] 예상치 못한 Principal 타입: {} ",
                authentication.getPrincipal().getClass());
        }
    }
}
