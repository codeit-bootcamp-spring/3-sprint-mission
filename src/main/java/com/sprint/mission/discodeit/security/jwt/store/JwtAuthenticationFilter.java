package com.sprint.mission.discodeit.security.jwt.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final JwtSessionRegistry jwtSessionRegistry;
    private final ObjectMapper objectMapper;
    private final DiscodeitUserDetailsService userDetailsService;

    private static final String FILTER_NAME = "[JwtAuthenticationFilter] ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info(FILTER_NAME + "요청 처리 시작: {} {}", request.getMethod(), request.getRequestURI());

            // Authorization 헤더에서 Bearer 토큰 추출
            String token = resolveToken(request);

            // 토큰 존재 유무 확인
            if (StringUtils.hasText(token)) {
                log.info(FILTER_NAME + "Bearer 토큰 추출 성공");

                // 액세스 토큰 유효성 검사(토큰 타입 검증, 만료 시간 검증, 서명 무결성 검증
                if (tokenProvider.validateAccessToken(token)) {

                    String jti = tokenProvider.getTokenId(token);

                    // 토큰 폐기 여부 확인
                    if (jwtSessionRegistry.isRevoked(jti)) {
                        log.debug(FILTER_NAME + "토큰이 폐기됨(revoked): jti={}", jti);;
                        sendUnAuthorized(response, "Token Revoked");

                        // 폐기 시 메서드 종료
                        return;
                    }

                    String username = tokenProvider.getUsernameFromToken(token);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 사용자 정보를 담은 토큰 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // 인증 객체에 현재 요청(request) 정보 추가
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 인증 객체를 SecurityContext에 저장
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info(FILTER_NAME + "SecurityContext 인증 설정 완료: username= {}", username);
                } else {
                    // 토큰 유효성 검사 실패 (401)
                    log.error(FILTER_NAME + "토큰 유효성 검사 실패");
                    sendUnAuthorized(response, "Invalid JWT");
                    return;
                }
            }

        } catch (Exception e) {
            // 인증 과정 예외 발생 시 인증 컨텍스트 초기화 및 401
            log.debug(FILTER_NAME + "예외 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendUnAuthorized(response, "JWT Authentication Failed");
            return;
        }

        // JWT 기반 인증 후 다음 필터 체인 진행
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendUnAuthorized(HttpServletResponse response, String message) throws IOException {

        // 응답 헤더 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String responseBody = objectMapper.createObjectNode()
                .put("success", false)
                .put("message", message)
                .toString();

        // JSON 응답 바디 전송
        response.getWriter().write(responseBody);
    }
}
