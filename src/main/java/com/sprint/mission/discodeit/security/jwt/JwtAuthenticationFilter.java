package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info("[JwtAuthenticationFilter] 요청 처리 시작: {} {}", request.getMethod(),
                request.getRequestURI());

            String token = resolveToken(request);

            if (StringUtils.hasText(token)) {
                log.info("[JwtAuthenticationFilter] Bearer 토큰 추출 성공");

                if (!tokenProvider.validateAccessToken(token)) {
                    sendUnauthorized(response, "Invalid access token");
                    return;
                }

                if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                    log.warn("[JwtAuthenticationFilter] 토큰이 JwtRegistry에서 비활성 상태 - 강제 로그아웃 처리");
                    sendUnauthorized(response, "Token has been invalidated - please log in again");
                    return;
                }

                log.info("[JwtAuthenticationFilter] JwtRegistry에서 토큰 활성 상태 확인됨");

                // 사용자 단위 활성 여부
                String username = tokenProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("[JwtAuthenticationFilter] SecurityContext 인증 설정 완료: username={}",
                    username);
            }
        } catch (Exception e) {
            // 인증 과정에서 예외 발생 시 인증 컨텍스트를 초기화하고 401 응답을 반환한다.
            System.out.println("[JwtAuthenticationFilter] 예외 발생: " + e.getMessage());
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "JWT authentication failed");
            return;
        }

        // JWT 기반 인증 후 다음 필터로 체인을 이어간다.
        filterChain.doFilter(request, response);
    }

    /**
     * 요청의 Authorization 헤더에서 Bearer 토큰을 파싱해 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 401 JSON 응답을 전송
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String responseBody = objectMapper.createObjectNode()
            .put("success", false)
            .put("message", message)
            .toString();

        response.getWriter().write(responseBody);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 정적 파일 & SPA & 공개 인증 엔드포인트는 JWT 필터 스킵
        if (uri.startsWith("/assets/") || uri.startsWith("/css/") || uri.startsWith("/js/")
            || uri.startsWith("/static/") || uri.equals("/") || uri.equals("/index.html")
            || uri.startsWith("/.well-known/") || uri.equals("/favicon.ico")) {
            return true;
        }

        // 공개 인증 엔드포인트만 제외 (role은 인증이 필요하므로 포함하지 않음)
        if (uri.equals("/api/auth/login") || uri.equals("/api/auth/refresh")
            || uri.equals("/api/auth/logout") || uri.equals("/api/auth/csrf-token")
            || (uri.equals("/api/users") && "POST".equals(request.getMethod()))) {
            return true;
        }

        return !uri.startsWith("/api/");
    }
}
