package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        try {
            DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateAccessToken(principal);

            String refreshToken = jwtTokenProvider.generateRefreshToken(principal);
            jwtTokenProvider.addRefreshCookie(response, refreshToken);

            // 응답 바디
            JwtDto body = new JwtDto(principal.getUserDto(), accessToken);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(response.getWriter(), body);

        } catch (Exception e) {
            throw new RuntimeException("로그인 성공 처리 중 오류", e);
        }
    }
}