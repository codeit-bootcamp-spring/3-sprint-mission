package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        log.debug("[JwtLoginSuccessHandler] onAuthenticationSuccess 시작: 응답 구성 준비");

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // UserDetails에서 사용자 정보 추출
        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            try {
                log.debug("[JwtLoginSuccessHandler] Jwt 토큰 발급 시작- username: {}",
                    userDetails.getUsername());

                jwtRegistry.invalidateJwtInformationByUserId(userDetails.getId());

                log.debug("[JwtLoginSuccessHandler] 새 토큰 발급 시작");
                String accessToken = tokenProvider.generateAccessToken(userDetails);
                String refreshToken = tokenProvider.generateRefreshToken(userDetails);

                JwtInformation jwtInformation = new JwtInformation(
                    userDetails.getUserResponseDto(),
                    accessToken,
                    refreshToken
                );

                jwtRegistry.registerJwtInformation(jwtInformation);
                
                // Refresh 쿠키 설정
                log.debug("[JwtLoginSuccessHandler] Refresh 쿠키 설정 시작");
                tokenProvider.addRefreshCookie(response, refreshToken);

                // JwtDto 전송
                UserResponseDto userResponseDto = userDetails.getUserResponseDto();
                JwtDto jwtDto = new JwtDto(userResponseDto, accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                log.debug("[JwtLoginSuccessHandler] 응답 전송됨");
            } catch (Exception e) {
                log.warn("[JwtLoginSuccessHandler] 예외 발생: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(objectMapper.createObjectNode()
                    .put("success", false)
                    .put("message", "Token generation failed")
                    .toString());
            }
        } else {
            log.warn("[JwtLoginSuccessHandler] Invalid Principal: {}",
                authentication.getPrincipal());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(objectMapper.createObjectNode()
                .put("success", false)
                .put("message", "Invalid principal")
                .toString());
        }
    }
}
