package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.event.payload.UserLoginEvent;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {

        try {
            DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
            UserDto userDto = principal.getUserDto();

            String accessToken = jwtTokenProvider.generateAccessToken(principal);
            String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

            jwtRegistry.invalidateJwtInformationByUserId(userDto.id());
            jwtRegistry.registerJwtInformation(
                    JwtInformation.builder()
                            .userDto(userDto)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build()
            );

            jwtTokenProvider.addRefreshCookie(response, refreshToken);

            JwtDto body = new JwtDto(userDto, accessToken);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), body);

            publisher.publishEvent(new UserLoginEvent(userDto.id()));

            log.info("[LOGIN] JWT 로그인 성공: user={}, uid={}", userDto.username(), userDto.id());

        } catch (Exception e) {
            throw new RuntimeException("로그인 성공 처리 중 오류", e);
        }
    }
}