package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;
    private final UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Authorization 헤더에서 JWT 토큰 추출
            String token = extractTokenFromRequest(request);

            // 토큰이 없으면 다음 필터로
            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // JWT 유효성 검사
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("유효하지 않은 JWT 입니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // Access 토큰인지 확인
            String tokenType = jwtTokenProvider.getTokenType(token);
            if (!"access".equals(tokenType)) {
                log.warn("액세스 토큰이 아닙니다! 토큰 타입 : {}", tokenType);
                filterChain.doFilter(request, response);
                return;
            }

            // JwtRegistry에서 토큰 상태 확인
            if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                log.warn("레지스트리에  등록되지 않은 토큰입니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에서 사용자명 추출
            String username = jwtTokenProvider.extractUsername(token);

            // 이미 인증된 상태가 아닌 경우에만 인증 처리
            if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {

                // UserDetails 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Authentication 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                // 요청 세부 정보 설정 ( IP, 세션 ID 등 )
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // JWT 토큰이 유효할 때 사용자를 온라인으로 표시
                if (userDetails instanceof DiscodeitUserDetails discodeitUserDetails) {
                    userSessionService.markUserOnline(discodeitUserDetails.getUserId());
                    log.debug("User {} marked as online", discodeitUserDetails.getUserId());
                }

                log.debug("JWT 인증 성공 : {}", username);
            }

        } catch (Exception e) {
                log.error("JWT 인증 처리 중 오류 발생", e);
                SecurityContextHolder.clearContext();
            }

            // 다음 필터로 진행
            filterChain.doFilter(request, response);
    }


    /**
     * HTTP 요청에서 JWT를 추출
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 토큰 반환
     * */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}