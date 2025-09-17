package com.sprint.mission.discodeit.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
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

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        log.info("[JwtLoginSuccessHandler] onAuthenticationSuccess 시작: 응답 구성 준비");

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails discodeitUserDetails) {
            try {
                UserDto userDto = discodeitUserDetails.getUserDto();

                // 1. 동일 계정 기존 토큰 전부 무효화(동시 로그인 제한)
                log.info("[JwtLoginSuccessHandler] 기존 토큰 무효화 시작 - username={}",
                    discodeitUserDetails.getUsername());
                if (request.getRequestURI().equals("/api/auth/login")) {
                    jwtRegistry.invalidateJwtInformationByUserId(userDto.id());
                }

                // 2. 새 Access/Refresh 발급
                log.info("[JwtLoginSuccessHandler] 새 토큰 발급 시작");
                String accessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
                String refreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);

                // 3. JwtRegistry에 새 토큰 정보 등록
                log.info("[JwtLoginSuccessHandler] 새 토큰 정보 등록 시작");
                JwtInformation jwtInformation = new JwtInformation(userDto, accessToken,
                    refreshToken);
                jwtRegistry.registerJwtInformation(jwtInformation);

                // 4. 리프레시 쿠키 설정
                log.info("[JwtLoginSuccessHandler] 리프레시 쿠키 설정 시작");
                Cookie refreshCookie = tokenProvider.generateRefreshTokenCookie(refreshToken);
                response.addCookie(refreshCookie);

                // 5. 성공 응답
                JwtDto jwtDto = new JwtDto(userDto, accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
                log.info("[JwtLoginSuccessHandler] onAuthenticationSuccess 완료: 응답 전송됨");
            } catch (Exception e) {
                // 예외 발생 시 처리(500)
                log.error("[JwtLoginSuccessHandler] 예외 발생: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(objectMapper.createObjectNode()
                    .put("success", false)
                    .put("message", "Token generation failed")
                    .toString());
            }
        } else {
            // 인증 실패 시 처리(401)
            log.error("[JwtLoginSuccessHandler] Invalid principal: {}",
                authentication.getPrincipal());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(objectMapper.createObjectNode()
                .put("success", false)
                .put("message", "Invalid principal")
                .toString());
        }
    }
}
