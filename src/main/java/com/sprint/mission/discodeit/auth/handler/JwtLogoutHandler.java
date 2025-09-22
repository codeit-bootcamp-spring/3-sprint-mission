package com.sprint.mission.discodeit.auth.handler;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        log.debug("[JwtLogoutHandler] 로그아웃 처리 시작: 리프레시 쿠키 만료 응답 추가");

        tokenProvider.expireRefreshCookie(response);

        if (authentication != null
            && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {

            jwtRegistry.invalidateJwtInformationByUserId(userDetails.userId());

            log.debug("[JwtLogoutHandler] JWT Registry에서 사용자 정보 제거 완료 - userId: {}",
                userDetails.userId());
        }

    }
}
