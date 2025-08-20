package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.utils.SessionContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final SessionContext sessionContext;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        System.out.println("[LoginSuccessHandler] 로그인 성공 처리 시작");
        if (authentication.getPrincipal() instanceof DiscodeitUserDetails discodeitUserDetails) {
            UserDto userDto = discodeitUserDetails.getUserDto();

            // 응답 헤더 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            //JSON 응답에 userDto값 추가
            String responseBody = objectMapper.writeValueAsString(userDto);
            response.getWriter().write(responseBody);

            System.out.println("[LoginSuccessHandler] 로그인 성공 응답 완료: " + userDto.getEmail());
            System.out.println(
                "[LoginSuccessHandler] 로그인 성공 응답 완료 - 세션리스트 : " + sessionContext.getSessionIds(
                    userDto.getId()).toString());
        } else {
            // 예상치 못한 principal인 경우
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"인증 정보를 처리할 수 없습니다.\"}");

            System.err.println(
                "[LoginSuccessHandler] 예상치 못한 Principal 타입: " + authentication.getPrincipal()
                    .getClass());

        }

    }
}
