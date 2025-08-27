package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 핸들러
 * <p>
 * 로그인 성공 시 사용자 정보를 반환한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            log.error("예상치 못한 Principal 타입: {}, 값: {}",
                authentication.getPrincipal().getClass(),
                authentication.getPrincipal()
            );
            throw new IllegalStateException("인증된 사용자의 타입이 예상과 다릅니다.");
        }

        try {
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            jwtTokenProvider.addRefreshCookie(response, refreshToken);

            JwtInformation jwtInformation = new JwtInformation(
                userDetails.getUserDto(),
                accessToken,
                refreshToken
            );
            jwtRegistry.registerJwtInformation(jwtInformation);

            JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

            log.info("[JwtLoginSuccessHandler] 로그인 성공 및 AT/RT 발급 완료");

        } catch (Exception e) {
            log.error("[JwtLoginSuccessHandler] 토큰 발급 중 오류 발생", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 발급 실패");
        }
    }
}