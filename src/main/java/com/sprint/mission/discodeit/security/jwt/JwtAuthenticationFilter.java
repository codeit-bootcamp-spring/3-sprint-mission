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

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info("[JwtAuthenticationFilter] 요청 처리 시작: " + request.getMethod() + " " + request.getRequestURI());

            String token = resolveToken(request);

            if (StringUtils.hasText(token)) {
                if (tokenProvider.validateAccessToken(token)) {
                    if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                        log.warn("[JwtAuthenticationFilter] 레지스트리에 존재하지 않는 토큰: accessToken={}", token);
                        sendUnauthorized(response, "만료되었거나 무효화된 토큰입니다.");
                        return;
                    }

                    String username = tokenProvider.getUsernameFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("[JwtAuthenticationFilter] SecurityContext 인증 설정 완료: username=" + username);
                } else {
                    log.warn("[JwtAuthenticationFilter] 토큰 유효성 검사 실패");
                    sendUnauthorized(response, "유효하지 않은 JWT 토큰");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("[JwtAuthenticationFilter] 예외 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "JWT 인증 실패");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청의 Authorization 헤더에서 Bearer 토큰을 파싱해 반환한다.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 401 JSON 응답을 전송한다.
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
}