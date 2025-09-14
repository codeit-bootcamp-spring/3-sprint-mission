package com.sprint.mission.discodeit.auth.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.response.UserDto;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication)
        throws IOException, ServletException {

        log.info("[JwtLoginSuccessHandler] 로그인 성공 처리 시작");

        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            log.error("[JwtLoginSuccessHandler] 예상치 못한 Principal 타입: {}", authentication.getPrincipal().getClass());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // 사용자 정보 추출
            UserDto userDto = userDetails.getUserDto();
            
            // 디버그: 로그인한 사용자 권한 확인
            log.info("[JwtLoginSuccessHandler] 로그인 성공 사용자: {}, 권한: {}", 
                userDto.username(), userDto.role());

            // 토큰 생성 (Access, Refresh)
            String accessToken  = jwtTokenProvider.generateAccessToken(userDetails);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            // 인메모리 레지스트리 등록
            JwtInformation jwtInfo = new JwtInformation(userDto, accessToken, refreshToken);
            jwtRegistry.registerJwtInformation(jwtInfo);

            // RefreshToken 쿠키로 내려보내기
            jwtTokenProvider.addRefreshCookie(response, refreshToken);

            // 응답 바디
            JwtDto body = new JwtDto(userDto, accessToken);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), body);

            log.info("[JwtLoginSuccessHandler] 로그인 성공 응답 완료: username={}", userDto.username());

        } catch (JOSEException e) {
            log.error("[JwtLoginSuccessHandler] 토큰 생성 실패: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(),
                // 간단한 에러 응답
                java.util.Map.of(
                    "error", "TOKEN_GENERATION_FAILED",
                    "message", "로그인 토큰 생성에 실패했습니다."
                )
            );
        }
    }
}
