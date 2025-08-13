package com.sprint.mission.discodeit.security.authorization;

import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

  public boolean isSelf(UUID userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails details)) {
      return false;
    }
    return details.getUser().id().equals(userId);
  }
}
