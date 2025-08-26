package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 응답 인코딩/콘텐츠 타입 설정
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // Principal 유효성 확인 및 캐스팅
        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {

            try {
                String accessToken = tokenProvider.generateAccessToken(userDetails);
                String refreshToken = tokenProvider.generateRefreshToken(userDetails);

                JwtDto jwtDto = new JwtDto(
                        userDetails.getUserDto(),
                        accessToken
                );

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                jwtRegistry.registerJwtInformation(
                        new JwtInformation(
                                userDetails.getUserDto(),
                                accessToken,
                                refreshToken
                        )
                );

                log.info("JWT access and refresh tokens issued for user: {}", userDetails.getUsername());

            } catch (JOSEException e) {

                log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                ErrorResponse errorResponse = new ErrorResponse(
                        new RuntimeException("Token 생성 실패"),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );

                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(
                    new RuntimeException("인증 실패: Invalid user details"),
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
