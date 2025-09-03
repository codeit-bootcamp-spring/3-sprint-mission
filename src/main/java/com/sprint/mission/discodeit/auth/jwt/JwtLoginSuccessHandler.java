package com.sprint.mission.discodeit.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.dto.JwtDTO;
import com.sprint.mission.discodeit.auth.jwt.store.JwtSessionRegistry;
import com.sprint.mission.discodeit.auth.jwt.store.JwtTokenEntity;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.response.UserResponse;
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
    private final JwtSessionRegistry jwtSessionRegistry;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            log.warn("[JwtLoginSuccessHandler] Invalid principal: {}",
                authentication.getPrincipal());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(),
                objectMapper.createObjectNode()
                    .put("success", false)
                    .put("message", "Invalid principal"));
            return;
        }

        try {
            log.info("[JwtLoginSuccessHandler] login success: username={}",
                userDetails.getUsername());

            jwtSessionRegistry.revokeAllByUsername(userDetails.getUsername());

            String accessToken = tokenProvider.generateAccessToken(userDetails);
            String refreshToken = tokenProvider.generateRefreshToken(userDetails);

            JwtTokenEntity accessEntity = tokenProvider.toEntity(accessToken);
            JwtTokenEntity refreshEntity = tokenProvider.toEntity(refreshToken);
            jwtSessionRegistry.register(accessEntity);
            jwtSessionRegistry.register(refreshEntity);

            tokenProvider.addRefreshCookie(response, refreshToken);

            UserResponse userDto = userDetails.getUserResponse();
            JwtDTO jwtDto = new JwtDTO(userDto, accessToken);

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), jwtDto);

        } catch (Exception e) {
            log.error("[JwtLoginSuccessHandler] token generation failed: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                objectMapper.createObjectNode()
                    .put("success", false)
                    .put("message", "Token generation failed"));
        }
    }
}
