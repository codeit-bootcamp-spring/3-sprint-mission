package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private static final String HANDLER_NAME = "[JwtLogoutHandler] ";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @PostConstruct
    public void init() {
        log.info(HANDLER_NAME + "생성자 호출됨: 만료된 RefreshToken Cookie 생성 + Session Registry 생성");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        log.info(HANDLER_NAME + "로그아웃 처리 시작: RefreshToken Cookie 만료 응답 추가");

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(cookie -> {
                    if (jwtRegistry.hasActiveJwtInformationByRefreshToken(cookie.getValue())) {
                        String refreshToken = cookie.getValue();

                        UUID userId = jwtRegistry.findUserIdByRefreshToken(refreshToken);

                        if (userId != null) {
                            jwtRegistry.invalidateJwtInformationByUserId(userId);


                            jwtTokenProvider.expireRefreshToken(response);
                            jwtRegistry.clearExpiredJwtInformation();

                            log.info(HANDLER_NAME + "Refresh Token 무효화 완료");
                        } else {
                            log.info(HANDLER_NAME + "RefreshToken을 소유한 유저가 존재하지 않음");
                        }
                    } else {
                        log.info(HANDLER_NAME + "유효한 Refresh Token이 없음");
                    }
                });
    }
}
