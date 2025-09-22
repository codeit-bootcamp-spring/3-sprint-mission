package com.sprint.mission.discodeit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.repository.registry.JwtRegistry;
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

    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService discodeitUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            if (StringUtils.hasText(token)) {
                log.info("[JwtAuthenticationFilter] 토큰 추출 성공");

                if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                    sendUnauthorized(response, "유효하지 않은 토큰 입니다.");
                }
                // 토큰에서 사용자명 추출
                String username = jwtTokenProvider.getUsernameFromToken(token);

                // 사용자 정보 로드
                UserDetails userDetails = discodeitUserDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // 인증 객체에 현재 요청 정보를 추가
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 인증 객체를 SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("[JwtAuthenticationFilter] SecurityContext 인증 설정 완료. {}", authentication);
            }
        } catch (Exception e) {
            // 인증 과정에서 예외 발생 시 인증 컨텍스트를 초기화하고 401 응답을 반환한다.
            log.info("[JwtAuthenticationFilter] 예외 발생: {} ", e.getMessage());
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "JWT authentication failed");
            return;
        }

        // 다음 필터로 체인 진행
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
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
