package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 핸들러
 * <p>
 * 로그인 성공 시 사용자 정보를 반환한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails discodeitUserDetails)) {
            log.error("예상치 못한 Principal 타입: {}, 값: {}",
                authentication.getPrincipal().getClass(),
                authentication.getPrincipal()
            );
            throw new IllegalStateException("인증된 사용자의 타입이 예상과 다릅니다.");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        UserDto userDto = discodeitUserDetails.getUserDto();
        String responseBody = objectMapper.writeValueAsString(userDto);
        response.getWriter().write(responseBody);
    }
}