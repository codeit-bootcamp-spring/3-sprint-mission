package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.jwt.store.JwtDto;
import com.sprint.mission.discodeit.security.jwt.store.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
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
import java.time.Instant;
import java.util.UUID;

/**
 * JWT 기반 인증 성공 시 처리하는 핸들러입니다.
 * 
 * <p>사용자 로그인 성공 시 Access Token과 Refresh Token을 생성하고,
 * JWT 레지스트리에 토큰 정보를 등록하며, 응답을 구성합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>JWT 토큰 생성 (Access Token, Refresh Token)</li>
 *   <li>기존 토큰 무효화 (동시 로그인 제한)</li>
 *   <li>토큰 정보 레지스트리 등록</li>
 *   <li>리프레시 토큰 쿠키 설정</li>
 *   <li>JWT 응답 데이터 전송</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 * @see AuthenticationSuccessHandler
 */
@Slf4j
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String HANDLER_NAME = "[JwtLoginSuccessHandler] ";

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    /**
     * JwtLoginSuccessHandler를 생성합니다.
     * 
     * @param objectMapper JSON 직렬화를 위한 ObjectMapper
     * @param jwtTokenProvider JWT 토큰 생성 및 관리 컴포넌트
     * @param jwtRegistry JWT 토큰 상태 관리 레지스트리
     */
    public JwtLoginSuccessHandler(ObjectMapper objectMapper, JwtTokenProvider jwtTokenProvider, JwtRegistry jwtRegistry) {
        log.info(HANDLER_NAME + "생성자 호출됨: 응답 JSON 직렬화를 위한 매퍼, JWT 생성/쿠키 유틸리티, 토큰 상태 저장소 주입");
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtRegistry = jwtRegistry;
    }

    /**
     * 인증 성공 시 호출되는 메소드입니다.
     * 
     * <p>로그인 성공 시 다음 작업을 수행합니다:</p>
     * <ol>
     *   <li>기존 토큰 무효화 (동시 로그인 제한)</li>
     *   <li>새로운 Access Token과 Refresh Token 생성</li>
     *   <li>토큰 정보를 레지스트리에 등록</li>
     *   <li>리프레시 토큰을 쿠키에 설정</li>
     *   <li>JWT 응답 데이터를 클라이언트에 전송</li>
     * </ol>
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증 성공한 사용자 정보
     * @throws IOException I/O 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println(HANDLER_NAME + "onAuthenticationSuccess 시작: 응답 구성 준비");

        // 응답 인코딩/컨텐트 타입 설정
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            try {
                // 1. 동일 계정 기존 토큰 전부 무효화(동시 로그인 제한)
                UUID userId =  userDetails.getUserDto().id();
                log.info(HANDLER_NAME + "기존 토큰 무효화 시작 - username= {}", userId);
                jwtRegistry.invalidateJwtInformationByUserId(userId);

                // 2. 새 Access/Refresh 토큰 발급
                log.info(HANDLER_NAME + "새 토큰 발급 시작");
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

                // 3. 토큰 메타데이터 저장 (toEntity로 중복 제거)
                log.info(HANDLER_NAME + "토큰 메타데이터 저장 시작");
                UserDto userDto = userDetails.getUserDto();
                JwtInformation jwtInformation = new JwtInformation(userDto, accessToken,refreshToken);
                jwtRegistry.registerJwtInformation(jwtInformation);

                // 4. 리프레시 쿠키 설정
                log.info(HANDLER_NAME + "리프레시 쿠키 설정 시작");
                jwtTokenProvider.addRefreshCookie(response, refreshToken);


                // 5. JwtDto 바디 전송
                JwtDto jwtDto = new JwtDto(userDto, accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                log.info(HANDLER_NAME + "ouAuthenticationSuccess 완료: 응답 전송됨");

                System.out.println(HANDLER_NAME + "로그인 성공 응답 완료: " + userDto.username());
            } catch (JOSEException e) {
                // 예외 발생 시 처리(500)
                log.error(HANDLER_NAME + "유저 {}의 JWT 생성 중 예외 발생: {}", userDetails.getUsername(),  e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                ErrorResponse errorResponse = new ErrorResponse(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "500",
                        "토큰 생성 실패",
                        Instant.now(),
                        null);

                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        } else {
            // 인증 실패 시 처리(401)
            log.info(HANDLER_NAME + "Invalid principal: {}", authentication.getPrincipal());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "401",
                    "유저 인증 실패",
                    Instant.now(),
                    null);

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

            System.err.println(HANDLER_NAME + "예상치 못한 Principal 타입: " + authentication.getPrincipal().getClass());
        }
    }
}
