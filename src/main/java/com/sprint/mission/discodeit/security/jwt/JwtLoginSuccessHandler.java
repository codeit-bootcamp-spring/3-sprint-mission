package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        UserDto userDto = userDetails.getUserDto();

        log.info("JWT 로그인 성공 : {}", userDto.username());

        try {
            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            // JwtRegistry에 정보 등록 ( 동시 로그인 제한 처리 포함 )
            JwtInformation jwtInformation = new JwtInformation(userDto, accessToken, refreshToken);
            jwtRegistry.registerJwtInformation(jwtInformation);

            // 리프레시 토큰을 HTTP-Only 쿠키에 저장
            Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(request.isSecure());
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) jwtTokenProvider.getRefreshTokenValidityInSeconds());

            response.addCookie(refreshTokenCookie);

            // 응답 설정
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            // JwtDto 생성 및 응답
            JwtDto jwtDto = new JwtDto(userDto, accessToken);
            String jsonResponse = objectMapper.writeValueAsString(jwtDto);
            response.getWriter().write(jsonResponse);

            log.info("JWT 발급 완료 : userId= {}", userDto.id());
        } catch (Exception e) {
            log.error("JWT 토큰 생성 중 오류 발생: userId={}", userDto.id(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"토큰 생성 중 오류가 발생했습니다.\"}");
        }

    }

}
