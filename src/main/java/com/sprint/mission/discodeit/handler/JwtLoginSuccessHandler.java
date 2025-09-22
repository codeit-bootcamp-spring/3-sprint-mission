package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.repository.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("[JwtLoginSuccessHandler] onAuthenticationSuccess 호출");


        // response encoding / contentType 설정 및 응답구조 설정
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Principal 유효성 확인 및 캐스팅
        if (authentication.getPrincipal() instanceof DiscodeitUserDetails discodeitUserDetails) {
            try {
                UserDto userDto = discodeitUserDetails.getUserDto();

                // 기존 사용자의 토큰들을 블랙리스트에 추가 (중복 로그인 방지)
                if (jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())) {
                    log.info("[JwtLoginSuccessHandler] 기존 로그인 무효화: userId={}", userDto.id());
                    
                    // 기존 사용자의 모든 토큰을 만료 시키고, 블랙리스트에 추가
                    jwtRegistry.inValidJwtInformationByUserId(userDto.id());
                }

                // 새로운 accessToken, refreshToken 발급
                String accessToken = jwtTokenProvider.generateAccessToken(userDto);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDto);

                jwtRegistry.register(new JwtInformation(userDto, accessToken, refreshToken));

                JwtDto jwtDto = new JwtDto(userDto, accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
                response.addCookie(new Cookie("REFRESH_TOKEN", refreshToken));
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
