package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String HANDLER_NAME = "[LoginSuccessHandler] ";
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println(HANDLER_NAME + "로그인 성공 처리 시작");

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            UserDto userDto = userDetails.getUserDto();

            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            String responseBody = objectMapper.writeValueAsString(userDto);
            response.getWriter().write(responseBody);

            System.out.println(HANDLER_NAME + "로그인 성공 응답 완료: " + userDto.username());
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"인증 정보를 처리할 수 없습니다.\"}");

            System.err.println(HANDLER_NAME + "예상치 못한 Principal 타입: " + authentication.getPrincipal().getClass());
        }
    }
}
