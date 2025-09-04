package com.sprint.mission.discodeit.auth.handler;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.SseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final SseService sseService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String EVENT_NAME_USER_UPDATED = "users.updated";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        log.debug("[JwtLogoutHandler] 로그아웃 처리 시작: Refresh 쿠키 만료 응답 추가");

        // Refresh 토큰을 즉시 만료 시키는 쿠키를 응답에 추가
        jwtTokenProvider.expireRefreshCookie(response);

        Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
            .findFirst()
            .ifPresent(cookie -> {
                String refreshToken = cookie.getValue();
                UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
                User user = findUser(userId);

                jwtRegistry.invalidateJwtInformationByUserId(userId);
                sseService.broadcast(EVENT_NAME_USER_UPDATED, userMapper.toDto(user));
            });

        log.debug("[JwtLogoutHandler] 로그아웃 처리 완료");
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException(userId));
    }
}
