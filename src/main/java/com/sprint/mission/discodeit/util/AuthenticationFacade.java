package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationFacade {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        return userDetails.getUserDto().id();
    }
}
