package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
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

        if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails p) {
            jwtRegistry.invalidateJwtInformationByUserId(p.getUserDto().id());
        }

    }
}