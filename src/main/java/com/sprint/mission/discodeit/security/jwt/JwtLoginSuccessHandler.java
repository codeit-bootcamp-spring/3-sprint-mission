package com.sprint.mission.discodeit.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            try {

                //토큰 생성
                jwtTokenProvider.generateRefreshToken(userDetails,response);
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails, response);

                //JwtDto로 변환후 저장
                JwtDto jwtDto = new JwtDto(userDetails.getUserDto(),accessToken);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
