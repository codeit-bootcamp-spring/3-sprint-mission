package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.event.payload.UserLogoutEvent;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher publisher;


    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        jwtTokenProvider.expireRefreshCookie(response);

        String rt = jwtTokenProvider.resolveRefreshToken(request);
        if (rt != null && jwtRegistry.hasActiveJwtInformationByRefreshToken(rt)) {
            UUID uid = jwtRegistry.invalidateJwtInformationByRefreshToken(rt);
            if (uid != null) {
                publisher.publishEvent(new UserLogoutEvent(uid)); // ✅ 이벤트 발행
            }
            return;
        }

        if (authentication instanceof UserDetails d
                && d instanceof DiscodeitUserDetails p) {
            UUID userId = p.getUserDto().id();
            jwtRegistry.invalidateJwtInformationByUserId(p.getUserDto().id());
            publisher.publishEvent(new UserLogoutEvent(userId));

        } else {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String at = header.substring(7);
                if (jwtTokenProvider.validateAccessToken(at)) {
                    java.util.UUID uid = jwtTokenProvider.getUserId(at);
                    if (uid != null) {
                        jwtRegistry.invalidateJwtInformationByUserId(uid);
                        publisher.publishEvent(new UserLogoutEvent(uid));
                    }
                }
            }
        }
    }
}