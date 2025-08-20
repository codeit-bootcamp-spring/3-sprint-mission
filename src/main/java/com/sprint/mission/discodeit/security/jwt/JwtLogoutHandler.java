package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        jwtTokenProvider.expireRefreshCookie(response);

        String rt = jwtTokenProvider.resolveRefreshToken(request);
        if (rt != null && jwtRegistry.hasActiveJwtInformationByRefreshToken(rt)) {
            jwtRegistry.invalidateJwtInformationByRefreshToken(rt);
            return;
        }

        if (authentication instanceof org.springframework.security.core.userdetails.UserDetails d
                && d instanceof com.sprint.mission.discodeit.security.DiscodeitUserDetails p) {
            jwtRegistry.invalidateJwtInformationByUserId(p.getUserDto().id());
        } else {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String at = header.substring(7);
                if (jwtTokenProvider.validateAccessToken(at)) {
                    java.util.UUID uid = jwtTokenProvider.getUserId(at);
                    if (uid != null) {
                        jwtRegistry.invalidateJwtInformationByUserId(uid);
                    }
                }
            }
        }
    }
}