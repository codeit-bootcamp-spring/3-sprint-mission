package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserRepository userRepository;

    @CacheEvict(value = "users", allEntries = true)
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("[JwtLogoutHandler] logout 호출 됨");
        
        // 1. SecurityContext 초기화
        SecurityContextHolder.clearContext();
        
        // 2. Access 토큰을 블랙리스트에 추가
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String accessToken = authorization.substring(7);
            jwtTokenProvider.blacklistAccessToken(accessToken);
            log.info("[JwtLogoutHandler] Access 토큰을 블랙리스트에 추가");
        }
        
        // 3. Refresh 토큰을 블랙리스트에 추가
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("REFRESH_TOKEN".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    jwtTokenProvider.blacklistRefreshToken(cookie.getValue());
                    log.info("[JwtLogoutHandler] Refresh 토큰을 블랙리스트에 추가");
                    break;
                }
            }
        }
        
        // 4. Refresh 토큰 쿠키 만료
        jwtTokenProvider.expireRefreshCookie(response);
        
        // 5. Registry에서 사용자 정보 무효화
        if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails user) {
            log.info("[JwtLogoutHandler] 사용자 정보로 Registry 무효화: userId={}", user.getUserDto().id());
            jwtRegistry.inValidJwtInformationByUserId(user.getUserDto().id());
        } else {
            // authentication이 null인 경우, Refresh 토큰 쿠키에서 사용자 정보를 추출하여 Registry 무효화
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("REFRESH_TOKEN".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                        try {
                            // Refresh 토큰에서 사용자명 추출
                            String username = jwtTokenProvider.getUsernameFromToken(cookie.getValue());
                            log.info("[JwtLogoutHandler] Refresh 토큰에서 사용자명 추출: username={}", username);
                            
                            // 사용자명으로 사용자 ID를 찾아서 Registry 무효화
                            userRepository.findByUsername(username)
                                    .ifPresent(user -> {
                                        log.info("[JwtLogoutHandler] Refresh 토큰으로 Registry 무효화: userId={}, username={}", user.getId(), username);
                                        jwtRegistry.inValidJwtInformationByUserId(user.getId());
                                    });
                        } catch (Exception e) {
                            log.error("[JwtLogoutHandler] Refresh 토큰에서 사용자명 추출 실패", e);
                        }
                        break;
                    }
                }
            } else {
                log.warn("[JwtLogoutHandler] 쿠키가 없어서 Registry 무효화를 건너뜀");
            }
        }
        
        log.info("[JwtLogoutHandler] 로그아웃 처리 완료 - 모든 인증 정보 정리됨");
    }
}
