package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 기반 인증 필터.
 *
 * <p>요청 헤더(Authorization)에 Bearer 토큰이 포함되어 있으면,
 * 토큰 유효성을 검사하고 SecurityContext에 인증 정보 설정
 * 요청 당 한 번만 실행</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * 요청 당 한 번만 실행되는 JWT 인증 처리 메서드.
     *
     * <p>작업 흐름:</p>
     * <ol>
     *   <li>Authorization 헤더에 Bearer 토큰이 있는지 확인</li>
     *   <li>토큰 유효성 검사 (JwtTokenProvider 활용)</li>
     *   <li>유효한 경우 UserDetailsService로 사용자 조회</li>
     *   <li>UsernamePasswordAuthenticationToken 생성 후 SecurityContext에 인증 정보 설정</li>
     *   <li>필터 체인 계속 진행</li>
     * </ol>
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인 객체
     * @throws ServletException 서블릿 처리 중 발생한 예외
     * @throws IOException 입출력 처리 중 발생한 예외
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 필터 체인 계속
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 접두사 제거 후 토큰 추출
        String token = header.substring(7);

        try {
            // 토큰 유효성 검사
            if (!jwtTokenProvider.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에서 username(subject) 추출
            String username = jwtTokenProvider.getSubject(token);

            // UserDetails 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT authentication successful for user: {}", username);

        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
        }

        // 필터 체인 계속
        filterChain.doFilter(request, response);
    }
}