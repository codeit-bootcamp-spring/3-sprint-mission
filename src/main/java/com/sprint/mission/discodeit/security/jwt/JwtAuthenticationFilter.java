package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.jwt.store.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import jakarta.annotation.PostConstruct;
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

/**
 * JWT 기반 인증을 처리하는 필터입니다.
 * 
 * <p>HTTP 요청의 Authorization 헤더에서 Bearer 토큰을 추출하고,
 * 토큰의 유효성을 검증하여 Spring Security 컨텍스트에 인증 정보를 설정합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>Bearer 토큰 추출 및 검증</li>
 *   <li>JWT 토큰 유효성 검사</li>
 *   <li>사용자 인증 정보 설정</li>
 *   <li>SecurityContext 인증 객체 설정</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 * @see OncePerRequestFilter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String FILTER_NAME = "[JwtAuthenticationFilter] ";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final ObjectMapper objectMapper;
    private final DiscodeitUserDetailsService userDetailsService;

    /**
     * 필터 초기화를 수행합니다.
     */
    @PostConstruct
    public void init() {
        log.info(FILTER_NAME + "생성자 호출됨: 필터 초기화");
    }

    /**
     * HTTP 요청을 필터링하여 JWT 인증을 처리합니다.
     * 
     * <p>각 요청에 대해 다음 작업을 수행합니다:</p>
     * <ol>
     *   <li>Authorization 헤더에서 Bearer 토큰 추출</li>
     *   <li>토큰 유효성 검증 (JWT 레지스트리 확인)</li>
     *   <li>사용자 정보 로드 및 인증 객체 생성</li>
     *   <li>SecurityContext에 인증 정보 설정</li>
     * </ol>
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException I/O 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info(FILTER_NAME + "요청 처리 시작: {} {}", request.getMethod(), request.getRequestURI());

            // Authorization 헤더에서 Bearer 토큰 추출
            String token = resolveToken(request);

            // 토큰 존재 유무 확인
            if (StringUtils.hasText(token)) {
                log.info(FILTER_NAME + "Bearer 토큰 추출 성공");

                // 액세스 토큰 유효성 검사(토큰 타입 검증, 만료 시간 검증, 서명 무결성 검증)
                if (jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {

                    String username = jwtTokenProvider.getUsernameFromToken(token);

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

    /**
     * HTTP 요청의 Authorization 헤더에서 Bearer 토큰을 추출합니다.
     * 
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰 또는 null
     */
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
