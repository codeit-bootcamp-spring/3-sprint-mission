package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            try {
                String accessToken = tokenProvider.createAccessToken(userDetails);
                String refreshToken = tokenProvider.createRefreshToken(userDetails);

                Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
                response.addCookie(refreshTokenCookie);

                JwtDto jwtDto = new JwtDto(
                        userDetails.getUserDto(),
                        accessToken
                );

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));


            } catch (JOSEException e) {
                log.error("JWT 토큰 생성 실패: {}", e.getMessage(), e);
                sendErrorResponse(response, ErrorCode.TOKEN_GENERATION_FAILED);
            }
        } else {
            log.warn("인증 실패: 유효하지 않은 Principal 타입. 타입: {}", authentication.getPrincipal().getClass().getName());
            sendErrorResponse(response, ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpiration / 1000));
        return cookie;
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
