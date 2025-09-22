package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.exception.auth.InvalidPrincipalException;
import com.sprint.mission.discodeit.exception.auth.TokenGenerateFailedException;
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

        log.debug("[JwtLoginSuccessHandler] JWT 기반 로그인 성공 처리 시작");

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails discodeitUserDetails) {
            try {

                log.debug("[JwtLoginSuccessHandler] JWT 토큰 발급 시작 - username={}",
                    discodeitUserDetails.getUsername());

                jwtRegistry.invalidateJwtInformationByUserId(discodeitUserDetails.userId());

                log.debug("[JwtLoginSuccessHandler] 새 토큰 발급 시작");
                String accessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
                String refreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);

                JwtInformation jwtInformation = new JwtInformation(
                    discodeitUserDetails.getUserDto(),
                    accessToken,
                    refreshToken
                );

                jwtRegistry.registerJwtInformation(jwtInformation);

                log.debug("[JwtLoginSuccessHandler] 리프레시 쿠키 설정 시작");
                tokenProvider.addRefreshCookie(response, refreshToken);

                JwtDto jwtDto = JwtDto.of(discodeitUserDetails.getUserDto(), accessToken);
                String responseBody = objectMapper.writeValueAsString(jwtDto);

                response.getWriter().write(responseBody);

                log.debug("[JwtLoginSuccessHandler] JWT 로그인 성공 응답 완료 - username={}",
                    discodeitUserDetails.getUsername());

            } catch (Exception e) {
                log.error("[JwtLoginSuccessHandler] 예외 발생: {}", e.getMessage());
                throw new TokenGenerateFailedException(e.getMessage());
            }

        } else {
            log.warn("[JwtLoginSuccessHandler] Invalid principal: {}",
                authentication.getPrincipal());

            throw new InvalidPrincipalException("Principal이 DiscodeitUserDetails 형태가 아닙니다");
        }

    }
}
