package com.sprint.mission.discodeit.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            System.out.println("[JwtAuthenticationFilter] 요청 처리 시작: " + request.getMethod() + " " + request.getRequestURI());

            // Authorization 헤더에서 Bearer 토큰을 추출
            String token = resolveToken(request);

            // Bearer 토큰이 없으면 다음 필터로
            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 레지스트리 활성 토큰인지
            if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                log.debug("[JwtAuthenticationFilter] 레지스트리에 없는 AccessToken");
                sendUnauthorized(response, "유효하지 않은 토큰으로 접근하였습니다.");
                return;
            }

            // 토큰 유효성 검사
            if (!tokenProvider.validateAccessToken(token)) {
                log.debug("[JwtAuthenticationFilter] AccessToken 유효성 검사 실패");
                sendUnauthorized(response, "유효하지 않은 토큰으로 접근하였습니다.");
                return;
            }

            // username 추출
            String username = tokenProvider.getUsernameFromToken(token);
            if (!StringUtils.hasText(username)) {
                log.debug("[JwtAuthenticationFilter] subject(username) 추출 실패");
                sendUnauthorized(response, "유효하지 않은 토큰으로 접근하였습니다.");
                return;
            }

            // UserDetailsService를 통해 사용자 정보를 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("[JwtAuthenticationFilter] 인증 성공 - username={}", username);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.debug("[JwtAuthenticationFilter] 예외 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "JWT authentication failed");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

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
