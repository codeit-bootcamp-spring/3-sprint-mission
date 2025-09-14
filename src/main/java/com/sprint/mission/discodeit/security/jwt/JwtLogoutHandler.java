package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * JWT 기반 로그아웃을 처리하는 핸들러입니다.
 * 
 * <p>사용자 로그아웃 시 리프레시 토큰을 무효화하고, 
 * JWT 레지스트리에서 해당 사용자의 모든 토큰 정보를 제거합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>리프레시 토큰 쿠키에서 사용자 ID 추출</li>
 *   <li>JWT 레지스트리에서 사용자 토큰 정보 무효화</li>
 *   <li>리프레시 토큰 쿠키 만료 처리</li>
 *   <li>만료된 토큰 정보 정리</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 * @see LogoutHandler
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private static final String HANDLER_NAME = "[JwtLogoutHandler] ";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;

    /**
     * 핸들러 초기화를 수행합니다.
     */
    @PostConstruct
    public void init() {
        log.info(HANDLER_NAME + "생성자 호출됨: 만료된 RefreshToken Cookie 생성 + Session Registry 생성");
    }

    /**
     * 로그아웃 처리를 수행합니다.
     * 
     * <p>다음 작업을 순차적으로 수행합니다:</p>
     * <ol>
     *   <li>리프레시 토큰 쿠키에서 사용자 ID 추출</li>
     *   <li>JWT 레지스트리에서 해당 사용자의 모든 토큰 정보 무효화</li>
     *   <li>리프레시 토큰 쿠키 만료 처리</li>
     *   <li>만료된 토큰 정보 정리</li>
     * </ol>
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증 객체 (로그아웃 시에는 null일 수 있음)
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        log.info(HANDLER_NAME + "로그아웃 처리 시작: RefreshToken Cookie 만료 응답 추가");

        Cookie refreshTokenExpirationCookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(refreshTokenExpirationCookie);

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();
                    try {
                        UUID userId = jwtRegistry.findUserIdByRefreshToken(refreshToken);

                        if (userId != null) {
                            // 1. JWT 레지스트리에서 토큰 무효화
                            jwtRegistry.invalidateJwtInformationByUserId(userId);
                            log.info(HANDLER_NAME + "사용자 토큰 무효화 완료: userId={}", userId);

                            // 2. 사용자 관련 캐시 무효화
                            evictUserCache(userId);

                        } else {
                            log.warn(HANDLER_NAME + "Refresh Token에 해당하는 사용자를 찾을 수 없음: {}", refreshToken);
                        }
                    } catch (Exception e) {
                        log.error(HANDLER_NAME + "토큰 무효화 중 오류 발생: {}", e.getMessage(), e);
                    }
                });
    }

    /**
     * 사용자 관련 캐시를 무효화합니다.
     * 
     * @param userId 사용자 ID
     */
    private void evictUserCache(UUID userId) {
        try {
            // 사용자 정보 캐시 무효화
            Cache userCache = cacheManager.getCache("userById");
            if (userCache != null) {
                userCache.evict(userId);
                log.debug(HANDLER_NAME + "사용자 정보 캐시 무효화 완료 - userId: {}", userId);
            }

            // 전체 사용자 목록 캐시 무효화
            Cache usersCache = cacheManager.getCache("users");
            if (usersCache != null) {
                usersCache.clear();
                log.debug(HANDLER_NAME + "전체 사용자 목록 캐시 무효화 완료");
            }

            // UserDetails 캐시 무효화 (SecurityContext에서 username 조회)
            String username = getCurrentUsername();
            if (username != null) {
                Cache userDetailsCache = cacheManager.getCache("userDetailsByUsername");
                if (userDetailsCache != null) {
                    userDetailsCache.evict(username);
                    log.debug(HANDLER_NAME + "UserDetails 캐시 무효화 완료 - username: {}", username);
                }
            } else {
                log.debug(HANDLER_NAME + "현재 사용자명을 찾을 수 없어 UserDetails 캐시 무효화 생략 - userId: {}", userId);
            }

            log.info(HANDLER_NAME + "사용자 로그아웃 캐시 무효화 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.warn(HANDLER_NAME + "사용자 로그아웃 캐시 무효화 실패 - userId: {}", userId, e);
        }
    }

    /**
     * 현재 SecurityContext에서 사용자명을 가져옵니다.
     * 
     * @return 현재 사용자명, 인증되지 않은 경우 null
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails) {
                return ((DiscodeitUserDetails) authentication.getPrincipal()).getUsername();
            }
            
            return null;
        } catch (Exception e) {
            log.debug(HANDLER_NAME + "현재 사용자명 조회 실패", e);
            return null;
        }
    }
}
