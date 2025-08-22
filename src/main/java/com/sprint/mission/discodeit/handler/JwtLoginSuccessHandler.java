package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtDto;
import com.sprint.mission.discodeit.security.jwt.store.JwtSessionRegistry;
import com.sprint.mission.discodeit.security.jwt.store.JwtTokenEntity;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String HANDLER_NAME = "[JwtLoginSuccessHandler] ";
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtSessionRegistry jwtSessionRegistry;
    private final JwtTokenProvider tokenProvider;

    public JwtLoginSuccessHandler(ObjectMapper objectMapper, JwtTokenProvider jwtTokenProvider, JwtSessionRegistry jwtSessionRegistry, JwtTokenProvider tokenProvider) {
        log.info(HANDLER_NAME + "생성자 호출됨: 응답 JSON 직렬화를 위한 매퍼, JWT 생성/쿠키 유틸리티, 토큰 상태 저장소 주입");
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtSessionRegistry = jwtSessionRegistry;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println(HANDLER_NAME + "onAuthenticationSuccess 시작: 응답 구성 준비");

        // 응답 인코딩/컨텐트 타입 설정
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {


            try {
                // 1. 동일 계정 기존 토큰 전부 무효화(동시 로그인 제한)
                log.info(HANDLER_NAME + "기존 토큰 무효화 시작 - username= {}", userDetails.getUsername());
                jwtSessionRegistry.revokeAllByUsername(userDetails.getUsername());

                // 2. 새 Access/Refresh 토큰 발급
                log.info(HANDLER_NAME + "새 토큰 발급 시작");
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

                // 3. 토큰 메타데이터 저장 (toEntity로 중복 제거)
                log.info(HANDLER_NAME + "토큰 메타데이터 저장 시작");
                JwtTokenEntity accessEntity = tokenProvider.toEntity(accessToken);
                JwtTokenEntity refreshEntity = tokenProvider.toEntity(refreshToken);
                jwtSessionRegistry.register(accessEntity);
                jwtSessionRegistry.register(refreshEntity);

                // 4. 리프레시 쿠키 설정
                log.info(HANDLER_NAME + "리프레시 쿠키 설정 시작");
                jwtTokenProvider.addRefreshCookie(response, refreshToken);

                // 사용자 DTO 구성
                UserDto userDto = userDetails.getUserDto();

                // 5. JwtDto 바디 전송
                JwtDto jwtDto = new JwtDto(userDto, accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                log.info(HANDLER_NAME + "ouAuthenticationSuccess 완료: 응답 전송됨");

                System.out.println(HANDLER_NAME + "로그인 성공 응답 완료: " + userDto.username());
            } catch (JOSEException e) {
                // 예외 발생 시 처리(500)
                log.info(HANDLER_NAME + "예외 발생: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(objectMapper.createObjectNode()
                        .put("success", false)
                        .put("message", "Token generation failed")
                        .toString());
            }
        } else {
            // 인증 실패 시 처리(401)
            log.info(HANDLER_NAME + "Invalid principal: {}", authentication.getPrincipal());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(objectMapper.createObjectNode()
                    .put("success", false)
                    .put("message", "Invalid principal")
                    .toString());

            System.err.println(HANDLER_NAME + "예상치 못한 Principal 타입: " + authentication.getPrincipal().getClass());
        }
    }
}
