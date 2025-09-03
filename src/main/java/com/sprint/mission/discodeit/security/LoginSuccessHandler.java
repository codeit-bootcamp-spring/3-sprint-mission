package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 시 호출되는 핸들러.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>인증된 사용자 정보를 JSON 형태로 응답</li>
 *   <li>인증 정보가 올바르지 않으면 401 에러 JSON 반환</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    /**
     * 로그인 성공 시 호출되는 메서드.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param authentication 인증 정보 객체
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication)
        throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 인증 정보 검증
        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            // 정상 사용자라면 200 OK
            response.setStatus(HttpServletResponse.SC_OK);

            UserDto userDto = userDetails.getUserDto();
            response.getWriter().write(objectMapper.writeValueAsString(userDto));

        } else {
            // 검증 실패 시 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            ErrorResponse errorResponse = new ErrorResponse(
                new RuntimeException("Authentication failed: Invalid user details"),
                HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
