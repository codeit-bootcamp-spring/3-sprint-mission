package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


/**
 * JWT 로그인 성공 핸들러.
 *
 * <p>인증 성공 시 처리:</p>
 * <ul>
 *     <li>Access Token을 응답 JSON으로 반환</li>
 *     <li>Refresh Token을 HttpOnly 쿠키에 저장</li>
 *     <li>UserDto 정보 포함</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 인증 성공 시 호출됨.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param authentication 인증 객체
     * @throws IOException 서블릿 응답 작성 실패 시
     * @throws ServletException 서블릿 처리 중 예외 발생 시
     */
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication)
        throws IOException, ServletException {

        // 응답 설정 : UTF-8 & JSON
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            try {
                UserDto userDto = userDetails.getUserDto();

                // JWT 발급 (기존 createAccessToken / createRefreshToken 사용)
                String accessToken = jwtTokenProvider.createAccessToken(userDto.username());
                String refreshToken = jwtTokenProvider.createRefreshToken(userDto.username());

                // Refresh Token을 HttpOnly 쿠키로 저장
                Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);

                // 클라이언트 JS 접근 불가
                refreshCookie.setHttpOnly(true);

                // HTTPS 환경에서 ture 권장
                refreshCookie.setSecure(true);

                // 쿠키 경로
                refreshCookie.setPath("/");
                refreshCookie.setMaxAge((int) (jwtTokenProvider.getRefreshTokenValidityMs() / 1000));
                response.addCookie(refreshCookie);

                // Access Token + UserDto를 JSON 응답
                JwtDto jwtDto = new JwtDto(userDto, accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                log.info("JWT access and refresh tokens issued for user: {}", userDto.username());

            } catch (Exception e) {
                // JWT 발급 실패 처리
                log.error("Failed to generate JWT token for user", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                ErrorResponse errorResponse = new ErrorResponse(
                    new RuntimeException("Token generation failed"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        } else {
            // 인증 객체가 예상 타입이 아닌 경우
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(
                new RuntimeException("Authentication failed: Invalid user details"),
                HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}