package com.sprint.mission.discodeit.security.authorization;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

  private final MessageRepository messageRepository;

  public boolean isAuthor(UUID messageId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails details)) {
      return false;
    }
    return messageRepository.findById(messageId)
        .map(m -> m.getAuthor().getId().equals(details.getUser().id()))
        .orElse(false);
  }
}
