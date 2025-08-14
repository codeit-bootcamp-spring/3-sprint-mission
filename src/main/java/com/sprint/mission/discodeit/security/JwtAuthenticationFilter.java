package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final DiscodeitUserDetailsService userDetailsService;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            log.debug("[JwtAuthenticationFilter] 요청 처리 시작: {}", request.getMethod() +
                request.getRequestURI());

            // Authorization 헤더에서 Bearer 토큰 추출
            String token = resolveToken(request);

            // 토큰이 존재하는지 확인
            if (StringUtils.hasText(token)) {
                log.debug("[JwtAuthenticationFilter] Bearer 토큰 추출 성공");

                if (jwtTokenProvider.validateAccessToken(token)) {

                    // subject에 저장된 사용자명 추출
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    // 사용자 정보 가져오기
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    // 인증 객체에 현재 요청 정보를 추가
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    // 인증 객체를 SecurityContext에 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("[JwtAuthenticationFilter] SecurityContext 인증 설정 완료 username: {}",
                        username);
                } else {
                    log.warn("[JwtAuthenticationFilter] 토큰 유효성 검사 실패");
                    sendUnauthorized(response, "Invalid JWT Token");
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("[JwtAuthenticationFilter] 예외 발생 : {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "JWT authentication failed");
            return;
        }

        // JWT 기반 인증 후 다음 필터로 체인을 이어간다.
        filterChain.doFilter(request, response);
    }

    /**
     * 요청의 Authorization 헤더에서 Bearer 토큰을 파싱하여 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 401 JSON 응답 전송
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {

        // 응답 헤더 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // JSON 응답 전송
        String responseBody = objectMapper.createObjectNode()
            .put("success", false)
            .put("message", message)
            .toString();

        // 응답 바디 전송
        response.getWriter().write(responseBody);
    }
}
