package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Slf4j
@Component
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
            log.debug("[JwtAuthenticationFilter] 요청 처리 시작: {} {}", request.getMethod(),
                request.getRequestURI());

            String token = resolveToken(request);

            if (token != null) {
                log.debug("[Filter] AT(head/tail) = {}...{}",
                    token.substring(0, 12), token.substring(token.length() - 12));
                log.debug("[Filter] RegistryHas? {}",
                    jwtRegistry.hasActiveJwtInformationByAccessToken(token));
            }

            if (StringUtils.hasText(token)) {
                log.debug("[JwtAuthenticationFilter] Bearer 토큰 추출 성공");

                if (tokenProvider.validateAccessToken(token)
                    && jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {

                    String username = tokenProvider.getUsernameFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("[JwtAuthenticationFilter] SecurityContext 인증 설정 완료: username={}",
                        username);

                } else {
                    log.debug("[JwtAuthenticationFilter] 토큰 유효성 검사 실패");
                    sendUnauthorized(response, "유효하지 않은 토큰으로 접근하였습니다");
                    return;
                }
            }
        } catch (Exception e) {
            log.debug("[JwtAuthenticationFilter] 예외 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "JWT authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
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
