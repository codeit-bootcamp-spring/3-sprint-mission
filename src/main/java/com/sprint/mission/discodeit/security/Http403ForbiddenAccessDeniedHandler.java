package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 403 Forbidden 에러 응답을 처리하는 핸들러.
 *
 * <p>
 * 사용자가 인증은 되었으나 요청한 자원에 접근할 권한이 없는 경우,
 * JSON 형식의 에러 응답을 반환한다.
 * </p>
 */
@RequiredArgsConstructor
public class Http403ForbiddenAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * 403 Forbidden 에러 발생 시 JSON 형식으로 응답을 반환한다.
     *
     * @param request 클라이언트 요청 객체
     * @param response 서버 응답 객체
     * @param accessDeniedException 접근 거부 예외
     * @throws IOException 입출력 오류 발생 시
     * @throws ServletException 서블릿 처리 오류 발생 시
     */
    @Override
    public void handle(HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException)
        throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(accessDeniedException,
            HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}